package com.esp.jscreen.telnet;

import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.events.*;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
	* other holds the {@link com.esp.jscreen.events.EventObject Event} that
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
class ProgrammableTerminal extends TerminalControl
{
	/**
		* Holds the actual control sequences that the class must recognise.
		*/
	private List controlcodes;
	/**
		* Holds the event that a control sequence means. The position
		* is the same position as the control sequence's position in the
		* controlcodes array.
		*/
	private Map events;
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
		* @param base The connection we can send data back through.
		*/
	ProgrammableTerminal(TelnetConnection base)
	{
		super(base);
		controlcodes = new ArrayList();
		events = new HashMap();
		current = new StringBuffer();
		hackesc=true;
		guess=0;
		addEvent("\u0001",new ControlKeyEvent(connection,'A'));
		addEvent("\u0002",new ControlKeyEvent(connection,'B'));
		addEvent("\u0003",new ControlKeyEvent(connection,'C'));
		addEvent("\u0004",new ControlKeyEvent(connection,'D'));
		addEvent("\u0005",new ControlKeyEvent(connection,'E'));
		addEvent("\u0006",new ControlKeyEvent(connection,'F'));
		addEvent("\u0007",new ControlKeyEvent(connection,'G'));
		addEvent("\u000B",new ControlKeyEvent(connection,'K'));
		addEvent("\u000E",new ControlKeyEvent(connection,'N'));
		addEvent("\u000F",new ControlKeyEvent(connection,'O'));
		addEvent("\u0010",new ControlKeyEvent(connection,'P'));
		addEvent("\u0011",new ControlKeyEvent(connection,'Q'));
		addEvent("\u0012",new ControlKeyEvent(connection,'R'));
		addEvent("\u0013",new ControlKeyEvent(connection,'S'));
		addEvent("\u0014",new ControlKeyEvent(connection,'T'));
		addEvent("\u0015",new ControlKeyEvent(connection,'U'));
		addEvent("\u0016",new ControlKeyEvent(connection,'V'));
		addEvent("\u0017",new ControlKeyEvent(connection,'W'));
		addEvent("\u0018",new ControlKeyEvent(connection,'X'));
		addEvent("\u0019",new ControlKeyEvent(connection,'Y'));
		addEvent("\u001A",new ControlKeyEvent(connection,'Z'));
		addEvent("\u001BOP",new FunctionKeyEvent(connection,FunctionKeyEvent.F1));
		addEvent("\u001BOQ",new FunctionKeyEvent(connection,FunctionKeyEvent.F2));
		addEvent("\u001BOR",new FunctionKeyEvent(connection,FunctionKeyEvent.F3));
		addEvent("\u001BOS",new FunctionKeyEvent(connection,FunctionKeyEvent.F4));
		addEvent("\u001B[15~",new FunctionKeyEvent(connection,FunctionKeyEvent.F5));
		addEvent("\u001B[17~",new FunctionKeyEvent(connection,FunctionKeyEvent.F6));
		addEvent("\u001B[18~",new FunctionKeyEvent(connection,FunctionKeyEvent.F7));
		addEvent("\u001B[19~",new FunctionKeyEvent(connection,FunctionKeyEvent.F8));
		addEvent("\u001B[20~",new FunctionKeyEvent(connection,FunctionKeyEvent.F9));
		addEvent("\u001B[21~",new FunctionKeyEvent(connection,FunctionKeyEvent.F10));
		addEvent("\u001B[23~",new FunctionKeyEvent(connection,FunctionKeyEvent.F11));
		addEvent("\u001B[24~",new FunctionKeyEvent(connection,FunctionKeyEvent.F12));

		addEvent("\u001B[A",new CursorKeyEvent(connection,CursorKeyEvent.CURSORUP));
		addEvent("\u001B[B",new CursorKeyEvent(connection,CursorKeyEvent.CURSORDN));
		addEvent("\u001B[D",new CursorKeyEvent(connection,CursorKeyEvent.CURSORLT));
		addEvent("\u001B[C",new CursorKeyEvent(connection,CursorKeyEvent.CURSORRT));
		addEvent("\u000C",new CursorKeyEvent(connection,CursorKeyEvent.FORMFEED));
		addEvent("\u001B[2~",new CursorKeyEvent(connection,CursorKeyEvent.INSERT));
		addEvent("\u001B[1~",new CursorKeyEvent(connection,CursorKeyEvent.HOME));
		addEvent("\u001B[4~",new CursorKeyEvent(connection,CursorKeyEvent.END));
		addEvent("\u001B[5~",new CursorKeyEvent(connection,CursorKeyEvent.PGUP));
		addEvent("\u001B[6~",new CursorKeyEvent(connection,CursorKeyEvent.PGDN));

		addEvent("\r",new KeyEvent(connection,KeyEvent.NEWLINE));
		addEvent("\u001B[P",new KeyEvent(connection,KeyEvent.PAUSE));
		addEvent("\u0008",new KeyEvent(connection,KeyEvent.BACKSPACE));
		addEvent("\u0009",new KeyEvent(connection,KeyEvent.TAB));
		addEvent("\u007F",new KeyEvent(connection,KeyEvent.DELETE));

		addEvent("\u001BOA",new CursorKeyEvent(connection,CursorKeyEvent.CURSORUP));
		addEvent("\u001BOB",new CursorKeyEvent(connection,CursorKeyEvent.CURSORDN));
		addEvent("\u001BOD",new CursorKeyEvent(connection,CursorKeyEvent.CURSORLT));
		addEvent("\u001BOC",new CursorKeyEvent(connection,CursorKeyEvent.CURSORRT));
		addEvent("\u001B[3~",new KeyEvent(connection,KeyEvent.DELETE));
		addEvent("\u001B[22~",new FunctionKeyEvent(connection,FunctionKeyEvent.F10));

		addEvent("\u001B[[A",new FunctionKeyEvent(connection,FunctionKeyEvent.F1));
		addEvent("\u001B[[B",new FunctionKeyEvent(connection,FunctionKeyEvent.F2));
		addEvent("\u001B[[C",new FunctionKeyEvent(connection,FunctionKeyEvent.F3));
		addEvent("\u001B[[D",new FunctionKeyEvent(connection,FunctionKeyEvent.F4));
		addEvent("\u001B[[E",new FunctionKeyEvent(connection,FunctionKeyEvent.F5));
	}

	/**
		* Adds a control sequence to the list.
		*
		* @param code The control sequence.
		* @param event The event that the sequence means.
		*/
	private void addEvent(String code, EventObject event)
	{
		int pos = Collections.binarySearch(controlcodes,code);
		if (pos<0)
		{
			pos=-(pos+1);
		}
		controlcodes.add(pos,code);
		events.put(code,event);
	}

	public void processData(ByteBuffer buffer)
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
					connection.processEvent(new KeyEvent(connection,KeyEvent.ESCAPE));
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
							for (int loop=0; loop<current.length(); loop++)
							{
								connection.processEvent(new KeyEvent(connection,current.charAt(loop)));
							}
							current = new StringBuffer();
						}
					}
					else
					{
						EventObject event = (EventObject)events.get(current.toString());
						connection.processEvent((EventObject)event.clone());
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
		*
		* This is buggy right now.
		*/
	protected void setTextAttributes(ColourInfo attr, ByteBuffer buffer)
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
			buffer.put(data.toString().getBytes());
			last = new ColourInfo(attr);
		}
	}

	void clearScreen(ByteBuffer buffer)
	{
		buffer.put((byte)27);
		buffer.put((byte)91);
		buffer.put((byte)50);
		buffer.put((byte)74);
	}

	void clearLine(ByteBuffer buffer)
	{
		buffer.put((byte)27);
		buffer.put((byte)91);
		buffer.put((byte)50);
		buffer.put((byte)75);
	}

	void moveCursor(int column, int row, ByteBuffer buffer)
	{
		//System.out.println("Move to "+column+"x"+row);
		buffer.put((byte)27);
		buffer.put((byte)91);
		buffer.put(String.valueOf(row+1).getBytes());
		buffer.put((byte)59);
		buffer.put(String.valueOf(column+1).getBytes());
		buffer.put((byte)72);
	}
}
