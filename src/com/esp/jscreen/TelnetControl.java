package com.esp.jscreen;

import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;

/**
	* The TelnetControl class takes any data coming from the network and
	* extracts any telnet control codes from it. Any other data is passed
	* on to another class. The class works as a simple state machine,
	* so requires no thread of its own.
	*
	* @author Dave Townsend
	*/
class TelnetControl
{
  /**
   * The telnet character Interpret As Command.
   */
  private static final byte IAC = convert(0xFF);
  /**
   * The telnet command for DONT.
   */
  private static final byte DONT = convert(0xFE);
  /**
   * The telnet command for DO.
   */
  private static final byte DO = convert(0xFD);
  /**
   * The telnet command for WONT.
   */
  private static final byte WONT = convert(0xFC);
  /**
   * The telnet command for WILL.
   */
  private static final byte WILL = convert(0xFB);
  /**
   * The telnet command for SB.
   */
  private static final byte SB = convert(0xFA);
  /**
   * The telnet command for SE.
   */
  private static final byte SE = convert(0xF0);

  /**
   * The telnet option ECHO.
   */
  private static final byte ECHO = convert(0x01);
  /**
   * The telnet option SUPPRESS GO AHEAD.
   */
  private static final byte SUPPRESS_GA = convert(0x03);
  /**
   * The telnet option NAWS.
   */
  private static final byte NAWS = convert(0x1F);
  /**
   * The telnet option TERMINAL_TYPE.
   */
  private static final byte TERMINAL_TYPE = convert(0x18);

	/**
		* The socket that we can send data out to.
		*/
  private SocketChannel client;
	/**
		* We buffer all output to try to cut down on I/O
		*/
  private ByteBuffer outputbuffer;
	/**
		* The inputbuffer gets all data after telnet codes are extracted.
		* Rather than create it every time new data comes in, we just have
		* a global one.
		*/
  private ByteBuffer inputbuffer;
	/**
		* Remember if we are still connected.
		*/
  private boolean connected;
	/**
		* What state are we currently in.
		*/
  private int state;
	/**
		* When we are in the middle of reading a subnegotiation, we have to
		* know how many characters we have received.
		*/
  private int sbpos;
  /**
  	* This holds the received subnegotiation data.
  	*/
  private byte[] sbdata = new byte[32];
	/**
		* This holds which command the subnegotiation applies to.
		*/
  private byte sbcommand;
	/**
		* A simple code for the terminal type (i.e. vt100, ANSI etc.)
		*/
  private String termtype;
	/**
		* If we are not autodetecting the terminal then we must ignore what
		* the terminal claims to be.
		*/
  private boolean autodetectterm;
  /**
  	* This is the terminal data handler that we pass all data to that
  	* isnt telnet control codes.
  	*/
  private InputTerminalControl terminal;
  /**
  	* When we are started up the session is given to us, ready to be given
  	* to the terminal control for our detected terminal.
  	*/
  private Session basesession;
	/**
		* The current screen width.
		*/
  private int width;
	/**
		* The current screen height.
		*/
  private int height;
  
	/**
		* Normal state, not inside any control codes.
		*/
  private static final int STATE_NORMAL = 0;
	/**
		* We have just received an IAC character, could be the start of a
		* control code, or an escaped IAC character.
		*/
  private static final int STATE_IAC = 1;
	/**
		* We have just received a WILL negotiation, waiting for the command.
		*/
  private static final int STATE_WILL = 2;
	/**
		* We have just received a WONT negotiation, waiting for the command.
		*/
  private static final int STATE_WONT = 3;
	/**
		* We have just received a DO negotiation, waiting for the command.
		*/
  private static final int STATE_DO = 4;
	/**
		* We have just received a DONT negotiation, waiting for the command.
		*/
  private static final int STATE_DONT = 5;
  /**
  	* We are now in a subnegotiation, don't know the command yet.
  	*/
  private static final int STATE_SB = 6;
  /**
  	* In the subnegotiation, have the command and any amount of data.
  	*/
  private static final int STATE_SBCOMM = 7;
  /**
  	* Just received an IAC in the subnegotiation. Might be the end, or just
  	* an escaped IAC character.
  	*/
  private static final int STATE_SBIAC = 8;
	
	/**
		* A simple way of converting a value (0 - 255) to a byte (-128 - 127)
		*
		* @param value The value to be converted. Could be anything, but
		* less than 0 will become 0, and greater than 255 will be modulo 256.
		* @return The value as a byte to pass to the client.
		*/
	private static byte convert(int value)
	{
		if (value<0)
		{
			value=0;
		}
		if (value>255)
		{
			value=value%256;
		}
		if (value>=128)
		{
			return (byte)(-256+value);
		}
		else
		{
			return (byte)value;
		}
	}
	
  /**  
   * Sends a WILL telnet command to the client.
   *
   * @param code  The option we are going to perform
   */
  private void writeWill(byte code)
  {
  	if (outputbuffer.remaining()<3)
  	{
  		flushBuffer();
  	}
  	outputbuffer.put(IAC);
  	outputbuffer.put(WILL);
  	outputbuffer.put(code);
  }

  /**
   * Sends a WONT telnet command to the client.
   *
   * @param code  The option we are not going to perform
   */
  private void writeWont(byte code)
  {              
  	if (outputbuffer.remaining()<3)
  	{
  		flushBuffer();
  	}
  	outputbuffer.put(IAC);
  	outputbuffer.put(WONT);
  	outputbuffer.put(code);
  }              

  /**            
   * Sends a DO telnet command to the client.
   *             
   * @param code  The option we wish the client to perform
   */            
  private void writeDo(byte code)
  {              
  	if (outputbuffer.remaining()<3)
  	{
  		flushBuffer();
  	}
  	outputbuffer.put(IAC);
  	outputbuffer.put(DO);
  	outputbuffer.put(code);
  }              

  /**            
   * Sends a DONT telnet command to the client.
   *             
   * @param code  The option we dont want the client to perform
   */            
  private void writeDont(byte code)
  {              
  	if (outputbuffer.remaining()<3)
  	{
  		flushBuffer();
  	}
  	outputbuffer.put(IAC);
  	outputbuffer.put(DONT);
  	outputbuffer.put(code);
  }           
  
	/**
		* Sends a subnegotiation to the client.
		*
		* @param code The command to send.
		* @param data The actual data to be sent.
		* @param length The amount of data in the array.
		*/
  private void writeSub(byte code, byte[] data, int length)
  {
  	if (outputbuffer.remaining()<(5+length))
  	{
  		flushBuffer();
  	}
  	outputbuffer.put(IAC);
  	outputbuffer.put(SB);
  	outputbuffer.put(code);
  	outputbuffer.put(data,0,length);
  	outputbuffer.put(IAC);
  	outputbuffer.put(SE);
  }   

	/**
		* Called to handle a WILL command.
		*
		* @param code The code that the client WILL do.
		*/
	private void receivedWill(byte code)
	{
		if (code==TERMINAL_TYPE)
		{
			writeSub(TERMINAL_TYPE,new byte[] {1},1);
			flushBuffer();
		}
		else if ((code==NAWS)||(code==SUPPRESS_GA))
		{
		}
		else
		{
			System.out.println("WILL "+code);
		}
	}
	
	/**
		* Called to handle a WONT command.
		*
		* @param code The code that the client WONT do.
		*/
	private void receivedWont(byte code)
	{
		if (code==ECHO)
		{
		}
		else
		{
			System.out.println("WONT "+code);
		}
	}
	
	/**
		* Called to handle a DO command.
		*
		* @param code The code that the client wants us to DO.
		*/
	private void receivedDo(byte code)
	{
		if ((code==SUPPRESS_GA)||(code==ECHO))
		{
		}
		else
		{
			System.out.println("DO "+code);
		}
	}
	
	/**
		* Called to handle a DONT command.
		*
		* @param code The code that the client doesnt want us to do.
		*/
	private void receivedDont(byte code)
	{
		System.out.println("DONT "+code);
	}
	
	/**
		* When we have been told what terminal type to use, we must set it up
		* with the session (from the old terminal if necessary).
		*/
	private void setupTerminal()
	{
		if (terminal!=null)
		{
			basesession=terminal.getSession();
		}
		terminal = new ProgrammableTerminal(this,basesession);
	}
	
	/**
		* Called to handle a subnegotiation.
		*
		* @param code The code for the subnegotiation.
		* @param data The data of the subnegotiation.
		* @param length The amount of data in the subnegotiation.
		*/
	private void receivedSub(byte code, byte[] data, int length)
	{
		if ((code==TERMINAL_TYPE)&&(data[0]==0))
		{
			termtype = new String(data,1,length);
			setupTerminal();
		}
		else if ((code==NAWS)&&(length==4))
		{
			int width = data[0]*256+data[1];
			int height = data[2]*256+data[3];
			setSize(width,height);
		}
		else
		{
			System.out.print("SB "+code+" ");
			System.out.println(new String(data,0,length));
		}
	}
	
	/**
		* Initialises the Telnet Control.
		*
		* @param client The network client.
		* @param base The initial session for the client to access.
		*/
	public TelnetControl(SocketChannel client, Session base)
	{
		this.client=client;
		outputbuffer = ByteBuffer.allocate(128);
		inputbuffer = ByteBuffer.allocate(128);
		connected=true;
		state=STATE_NORMAL;
		width=80;
		height=25;
		sbpos=0;
		termtype="UNKNOWN";
		terminal=null;
		autodetectterm=false;
		writeWill(SUPPRESS_GA);
		writeDo(SUPPRESS_GA);
		writeDont(ECHO);
		writeWill(ECHO);
		writeDo(NAWS);
		basesession=base;
		flushBuffer();
	}
	
	/**
		* Tells the telnet control to autodetect the terminal type.
		*/
	public void detectTerminal()
	{
		autodetectterm=true;
		writeDo(TERMINAL_TYPE);
		flushBuffer();
	}
	
	/**
		* Set a new size for the client.
		* Possible should be private?
		*
		* @param width The new width.
		* @param height The new height.
		*/
	public void setSize(int width, int height)
	{
		this.width=width;
		this.height=height;
		if (terminal!=null)
		{
			terminal.setSize(width,height);
		}
	}
	
	/**
		* Returns the width of the client.
		*
		* @return The width.
		*/
	public int getWidth()
	{
		return width;
	}
	
	/**
		* Returns the height of the client.
		*
		* @return The height.
		*/
	public int getHeight()
	{
		return height;
	}
	
	/**
		* Indicates that the client has disconnected.
		*/
	public void close()
	{
		connected=false;
		terminal.close();
	}
	
	/**
		* Forces the telnet control to send any data in the outputbuffer.
		*/
	public void flushBuffer()
	{
		if (connected)
		{
			outputbuffer.flip();
			try
			{
				while (outputbuffer.remaining()>0)
				{
					client.write(outputbuffer);
				}
			}
			catch (Exception e)
			{
				close();
			}
		}
		outputbuffer.clear();
	}
	
	/**
		* Places some data into the output buffer (flushing if it is full).
		*
		* @param buffer The data to be sent.
		*/
	public void sendData(ByteBuffer buffer)
	{
		if (buffer.remaining()>outputbuffer.remaining())
		{
			flushBuffer();
		}
		assert (buffer.remaining()<=outputbuffer.remaining());
		outputbuffer.put(buffer);
	}
	
	/**
		* Again sends data, but an array can be passed.
		*
		* @param buffer The array of data to send.
		*/
	public void sendData(byte[] buffer)
	{
		if (buffer.length>outputbuffer.remaining())
		{
			flushBuffer();
		}
		assert (buffer.length<=outputbuffer.remaining());
		outputbuffer.put(buffer);
	}
	
	/**
		* Sends a single byte of data.
		*
		* @param data The byte to send.
		*/
	public void sendData(byte data)
	{
		if (1>outputbuffer.remaining())
		{
			flushBuffer();
		}
		assert (outputbuffer.remaining()>0);
		outputbuffer.put(data);
	}
	
	/**
		* Called when new data comes in from the client.
		* Simple state machine.
		*
		* @param buffer The new data.
		*/
	public void newData(ByteBuffer buffer)
	{
		byte data;
		while (buffer.hasRemaining())
		{
			data=buffer.get();
			switch(state)
			{
				case STATE_IAC:			if (data==WILL)
														{
															state=STATE_WILL;
														}
														else if (data==WONT)
														{
															state=STATE_WONT;
														}
														else if (data==DO)
														{
															state=STATE_DO;
														}
														else if (data==DONT)
														{
															state=STATE_DONT;
														}
														else if (data==SB)
														{
															state=STATE_SB;
														}
														else if (data==IAC)
														{
															inputbuffer.put(data);
															state=STATE_NORMAL;
														}
														else
														{
															assert false;
														}
														break;
				case STATE_WILL:		receivedWill(data);
														state=STATE_NORMAL;
														break;
				case STATE_WONT:		receivedWont(data);
														state=STATE_NORMAL;
														break;
				case STATE_DO:			receivedDo(data);
														state=STATE_NORMAL;
														break;
				case STATE_DONT:		receivedDont(data);
														state=STATE_NORMAL;
														break;
				case STATE_SB:			sbcommand=data;
														state=STATE_SBCOMM;
														break;
				case STATE_SBCOMM:	if (data==IAC)
														{
															state=STATE_SBIAC;
														}
														else
														{
															sbdata[sbpos]=data;
															sbpos++;
														}
														break;
				case STATE_SBIAC:		if (data==SE)
														{
															receivedSub(sbcommand,sbdata,sbpos);
															sbpos=0;
															state=STATE_NORMAL;
														}
														else if (data==IAC)
														{
															sbdata[sbpos]=data;
															sbpos++;
															state=STATE_SBCOMM;
														}
														else
														{
															assert false;
														}
														break;
				case STATE_NORMAL:	if (data==IAC)
														{
															state=STATE_IAC;
														}
														else
														{
															inputbuffer.put(data);
														}
														break;
				default:						assert false;
			}
		}
		if ((terminal!=null)&&(inputbuffer.position()>0))
		{
			inputbuffer.flip();
			terminal.newData(inputbuffer);
			inputbuffer.clear();
		}
	}
}
