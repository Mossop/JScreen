package com.esp.jscreen;

import com.esp.jscreen.AbstractTerminalControl;
import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.commands.*;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
	* The ProgrammableTerminal class is designed to be able to
	* decode control codes from any terminal type. This has been tested
	* on vt100 and ansi terminals so far. In theory a configuration file
	* can be used to set the class up for any other terminal type.<br>
	*
	* One problem with most terminals is that the ESC character is the
	* start of many control sequences. This is fine until you are trying to
	* detect when the user presses the ESC key. Unlike any sensible system
	* where some form of escaping would occur (sending two ESC characters
	* at the very least), vt100, linux and ansi terminals just send the
	* single character. To try to handle this, the class has a hack that
	* can be enabled. When an ESC character is received and the hack is
	* enabled, if there is no more data in the input buffer, it is assumed
	* that the user pressed ESC. Otherwise the class checks the further data
	* before it makes its decision. This assumes that any terminal sending
	* control sequences sends them in one go, not a character at a time.
	* So far this has proved to work fine.<br>
	*
	* The control sequences that the class recognises from the clients end
	* are held in a pair of ArrayLists. One holds the sequence, while the
	* other holds the {@link com.esp.jscreen.commands.Command Command} that
	* it relates to. The lists are sorted by control sequences, and a
	* binary search technique is used to find the correct sequence from the
	* input data. If the data matches the start of any sequence, then nothing
	* happens, the result is remembered and revised when new data arrives.
	* If the data does not match the start of any sequence, then it is assumed
	* to be text typed in. In this way even if the client's terminal send
	* control sequences a character at a time, the sequence will still be
	* detected.
	*
	* @author Dave Townsend
	*/
class ProgrammableTerminal extends AbstractTerminalControl
{
	/**
		* Holds the actual control sequences that the class must recognise.
		*/
	private List controlcodes;
	/**
		* Holds the command that a control sequence means. The position
		* is the same position as the control sequence's position in the
		* controlcodes array.
		*/
	private List commands;
	/**
		* Holds our current guess at the incoming control sequence.
		*/
	private int guess;
	/**
		* Holds the current data we have received that may be a control sequence.
		*/
	private StringBuffer current;
	/**
		* Holds whether or not to use our hack for detecting the ESC character.
		*/
	private boolean hackesc;
	/**
		* Remembers what colour the terminal is currently writing in. Saves us
		* sending some data updating the colour when it isnt necessary.
		*/
	private ColourInfo last;
	
	/**
		* Creates the object. Also hardcoded here for now are the control
		* sequences.
		*
		* @param client The client we can send data back through.
		* @param session The session that the client is using.
		*/
	public ProgrammableTerminal(TelnetControl client, Session session)
	{
		super(client,session);
		controlcodes = new ArrayList();
		commands = new ArrayList();
		current = new StringBuffer();
		hackesc=true;
		guess=0;
		addCommand("\u0001",new ControlCommand('A'));
		addCommand("\u0002",new ControlCommand('B'));
		addCommand("\u0003",new ControlCommand('C'));
		addCommand("\u0004",new ControlCommand('D'));
		addCommand("\u0005",new ControlCommand('E'));
		addCommand("\u0006",new ControlCommand('F'));
		addCommand("\u0007",new ControlCommand('G'));
		addCommand("\u000B",new ControlCommand('K'));
		addCommand("\u000E",new ControlCommand('N'));
		addCommand("\u000F",new ControlCommand('O'));
		addCommand("\u0010",new ControlCommand('P'));
		addCommand("\u0011",new ControlCommand('Q'));
		addCommand("\u0012",new ControlCommand('R'));
		addCommand("\u0013",new ControlCommand('S'));
		addCommand("\u0014",new ControlCommand('T'));
		addCommand("\u0015",new ControlCommand('U'));
		addCommand("\u0016",new ControlCommand('V'));
		addCommand("\u0017",new ControlCommand('W'));
		addCommand("\u0018",new ControlCommand('X'));
		addCommand("\u0019",new ControlCommand('Y'));
		addCommand("\u001A",new ControlCommand('Z'));
		addCommand("\u001BOP",new FunctionCommand(1));
		addCommand("\u001BOQ",new FunctionCommand(2));
		addCommand("\u001BOR",new FunctionCommand(3));
		addCommand("\u001BOS",new FunctionCommand(4));
		addCommand("\u001B[15~",new FunctionCommand(5));
		addCommand("\u001B[17~",new FunctionCommand(6));
		addCommand("\u001B[18~",new FunctionCommand(7));
		addCommand("\u001B[19~",new FunctionCommand(8));
		addCommand("\u001B[20~",new FunctionCommand(9));
		addCommand("\u001B[21~",new FunctionCommand(10));
		addCommand("\u001B[23~",new FunctionCommand(11));
		addCommand("\u001B[24~",new FunctionCommand(12));
		
		addCommand("\u001B[A",new CursorCommand(CursorCommand.CC_CURSORUP));
		addCommand("\u001B[B",new CursorCommand(CursorCommand.CC_CURSORDN));
		addCommand("\u001B[D",new CursorCommand(CursorCommand.CC_CURSORLT));
		addCommand("\u001B[C",new CursorCommand(CursorCommand.CC_CURSORRT));
		addCommand("\u000C",new CursorCommand(CursorCommand.CC_FORMFEED));
		addCommand("\u001B[2~",new CursorCommand(CursorCommand.CC_INSERT));
		addCommand("\u001B[1~",new CursorCommand(CursorCommand.CC_HOME));
		addCommand("\u001B[4~",new CursorCommand(CursorCommand.CC_END));
		addCommand("\u001B[5~",new CursorCommand(CursorCommand.CC_PGUP));
		addCommand("\u001B[6~",new CursorCommand(CursorCommand.CC_PGDN));

		addCommand("\r",new TextCommand(TextCommand.TC_NEWLINE));
		addCommand("\u001B[P",new TextCommand(TextCommand.TC_PAUSE));
		addCommand("\u0008",new TextCommand(TextCommand.TC_BACKSPACE));
		addCommand("\u0009",new TextCommand(TextCommand.TC_TAB));
		addCommand("\u007F",new TextCommand(TextCommand.TC_DELETE));

		addCommand("\u001BOA",new CursorCommand(CursorCommand.CC_CURSORUP));
		addCommand("\u001BOB",new CursorCommand(CursorCommand.CC_CURSORDN));
		addCommand("\u001BOD",new CursorCommand(CursorCommand.CC_CURSORLT));
		addCommand("\u001BOC",new CursorCommand(CursorCommand.CC_CURSORRT));
		addCommand("\u001B[3~",new TextCommand(TextCommand.TC_DELETE));
		addCommand("\u001B[22~",new FunctionCommand(10));

		addCommand("\u001B[[A",new FunctionCommand(1));
		addCommand("\u001B[[B",new FunctionCommand(2));
		addCommand("\u001B[[C",new FunctionCommand(3));
		addCommand("\u001B[[D",new FunctionCommand(4));
		addCommand("\u001B[[E",new FunctionCommand(5));
	}
	
	/**
		* Adds a control sequence to the list.
		*
		* @param code The control sequence.
		* @param command The command that the sequence means.
		*/
	private void addCommand(String code, Command command)
	{
		int pos = Collections.binarySearch(controlcodes,code);
		if (pos<0)
		{
			pos=-(pos+1);
		}
		controlcodes.add(pos,code);
		commands.add(pos,command);
	}
	
	public void newData(ByteBuffer buffer)
	{
		ByteBuffer returnb = ByteBuffer.allocate(128);
		byte[] data = new byte[1];
		while (buffer.hasRemaining())
		{
			data[0]=buffer.get();
			if (data[0]!=10)
			{
				current.append(new String(data));
				if ((data[0]==27)&&(current.length()==1)&&(!buffer.hasRemaining())&&(hackesc))
				{
					session.newCommand(new TextCommand(TextCommand.TC_ESCAPE));
					current = new StringBuffer();
				}
				else
				{
					guess=Collections.binarySearch(controlcodes,current.toString());
					if (guess<0)
					{
						guess=-(guess+1);
						if ((guess>=controlcodes.size())||(!((String)controlcodes.get(guess)).startsWith(current.toString())))
						{
							session.newText(current);
							current = new StringBuffer();
						}
					}
					else
					{
						session.newCommand((Command)commands.get(guess));
						current = new StringBuffer();
					}
				}
			}
		}
	}

	/**
		* Changes the colour that the client's terminal is printing in.
		* We check against the last colour we sent and only update the
		* parts that need to be updated.
		*/
	protected void setTextAttributes(ColourInfo attr)
	{
		if (last==null)
		{
			last = new ColourInfo();
		}
		StringBuffer data = new StringBuffer('\u001B'+"[");
		if (attr.getBold()!=last.getBold())
		{
			data.append(";");
			if (!attr.getBold())
			{
				data.append("2");
			}
			data.append("1");
		}
		if (attr.getFlash()!=last.getFlash())
		{
			data.append(";");
			if (!attr.getFlash())
			{
				data.append("2");
			}
			data.append("5");
		}
		if (attr.getUnderline()!=last.getUnderline())
		{
			data.append(";");
			if (!attr.getUnderline())
			{
				data.append("2");
			}
			data.append("4");
		}
		if (attr.getForeground()!=last.getForeground())
		{
			data.append(";3"+attr.getForeground());
		}
		if (attr.getBackground()!=last.getBackground())
		{
			data.append(";4"+attr.getBackground());
		}
		if (data.length()>2)
		{
			data.append("m");
			client.sendData(data.toString().getBytes());
			last = new ColourInfo(attr);
		}
	}
	
	/**
		* {@inheritDoc}
		* <br>
		* This currently does nothing, none of the terminals I have tested
		* seem to respond to the sequence I am sending. Many display garbage.
		*/
	public void setCursorVisibility(boolean vis)
	{
		/*client.sendData((byte)27);
		if (vis)
		{
			client.sendData((byte)55);
		}
		else
		{
			client.sendData((byte)54);
		}
		client.sendData((byte)112);
		client.flushBuffer();*/
	}
	
	public void clearScreen()
	{
		client.sendData((byte)27);
		client.sendData((byte)91);
		client.sendData((byte)50);
		client.sendData((byte)74);
		client.flushBuffer();
	}
	
	public void clearLine()
	{
		client.sendData((byte)27);
		client.sendData((byte)91);
		client.sendData((byte)50);
		client.sendData((byte)75);
	}
	
	public void moveCursor(int row, int column)
	{
		client.sendData((byte)27);
		client.sendData((byte)91);
		client.sendData(String.valueOf(row+1).getBytes());
		client.sendData((byte)59);
		client.sendData(String.valueOf(column+1).getBytes());		
		client.sendData((byte)72);
		client.flushBuffer();
	}
}
