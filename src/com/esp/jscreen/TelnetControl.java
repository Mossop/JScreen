package com.esp.jscreen;

import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;

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

  private SocketChannel client;
  private ByteBuffer outputbuffer;
  private ByteBuffer inputbuffer;
  private boolean connected;
  private int state;
  private int sbpos;
  private byte[] sbdata = new byte[32];
  private byte sbcommand;
  private String termtype;
  private boolean autodetectterm;
  private InputTerminalControl terminal;
  private Session basesession;
  private int width;
  private int height;
  
  private static final int STATE_NORMAL = 0;
  private static final int STATE_IAC = 1;
  private static final int STATE_WILL = 2;
  private static final int STATE_WONT = 3;
  private static final int STATE_DO = 4;
  private static final int STATE_DONT = 5;
  private static final int STATE_SB = 6;
  private static final int STATE_SBCOMM = 7;
  private static final int STATE_SBIAC = 8;
	
	private static byte convert(int value)
	{
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
	
	private void receivedDont(byte code)
	{
		System.out.println("DONT "+code);
	}
	
	private void setupTerminal()
	{
		if (terminal!=null)
		{
			basesession=terminal.getSession();
		}
		terminal = new ProgrammableTerminal(this,basesession);
	}
	
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
	
	public void detectTerminal()
	{
		autodetectterm=true;
		writeDo(TERMINAL_TYPE);
		flushBuffer();
	}
	
	public void setSize(int width, int height)
	{
		this.width=width;
		this.height=height;
		if (terminal!=null)
		{
			terminal.setSize(width,height);
		}
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void close()
	{
		connected=false;
		terminal.close();
	}
	
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
	
	public void sendData(ByteBuffer buffer)
	{
		if (buffer.remaining()>outputbuffer.remaining())
		{
			flushBuffer();
		}
		assert (buffer.remaining()<=outputbuffer.remaining());
		outputbuffer.put(buffer);
	}
	
	public void sendData(byte[] buffer)
	{
		if (buffer.length>outputbuffer.remaining())
		{
			flushBuffer();
		}
		assert (buffer.length<=outputbuffer.remaining());
		outputbuffer.put(buffer);
	}
	
	public void sendData(byte data)
	{
		if (1>outputbuffer.remaining())
		{
			flushBuffer();
		}
		assert (outputbuffer.remaining()>0);
		outputbuffer.put(data);
	}
	
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
