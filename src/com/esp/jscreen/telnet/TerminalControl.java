package com.esp.jscreen.telnet;

import java.nio.ByteBuffer;
import java.util.Iterator;
import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.StringColourPair;

/**
	* This is the starting point for any terminal control implementation.
	*/
abstract class TerminalControl
{
	/**
		* Remembers our connection.
		*/
	protected TelnetConnection connection;
	
	/**
		* Simply sets up the session and client variables, and tells the
		* session about this object.
		*/
	TerminalControl(TelnetConnection base)
	{
		this.connection=base;
	}
	
	abstract void processData(ByteBuffer buffer);
	
	void beep(ByteBuffer buffer)
	{
		buffer.put((byte)7);
	}
	
	/**
		* This method is used by the functions sending coloured text to
		* the client. It will be different for many terminals so we
		* just force the subclass to define it.
		*
		* @param attr The colour information to send to the client.
		*/
	protected abstract void setTextAttributes(ColourInfo attr, ByteBuffer buffer);
	
	/**
		* This currently does nothing, none of the terminals I have tested
		* seem to respond to the sequence I am sending. Many display garbage.
		*/
	void setCursorVisibility(boolean vis, ByteBuffer buffer)
	{
	}
	
	abstract void clearScreen(ByteBuffer buffer);
	
	abstract void clearLine(ByteBuffer buffer);
	
	abstract void moveCursor(int row, int column, ByteBuffer buffer);
	
	void writeText(ColouredString text, ByteBuffer buffer)
	{
		Iterator loop = text.iterator();
		StringColourPair pair;
		while (loop.hasNext())
		{
			pair = (StringColourPair)loop.next();
			setTextAttributes(pair.getColour(),buffer);
			buffer.put(pair.toString().getBytes());
		}
	}
	
	void writeLine(ColouredString text, ByteBuffer buffer)
	{
		clearLine(buffer);
		writeText(text,buffer);
	}
	
	void writeText(String text, ByteBuffer buffer)
	{
		buffer.put(text.getBytes());
	}
	
	void writeLine(String text, ByteBuffer buffer)
	{
		clearLine(buffer);
		writeText(text,buffer);
	}
}
