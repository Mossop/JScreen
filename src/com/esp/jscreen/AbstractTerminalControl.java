package com.esp.jscreen;

import java.nio.ByteBuffer;
import java.util.Iterator;
import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.StringColourPair;

abstract class AbstractTerminalControl implements InputTerminalControl, OutputTerminalControl
{
	protected TelnetControl client;
	protected Session session;
	
	public AbstractTerminalControl(TelnetControl client, Session session)
	{
		this.client=client;
		this.session=session;
		session.setTerminal(this);
	}
	
	public Session getSession()
	{
		return session;
	}
	
	public void setSession(Session newsession)
	{
		session.close();
		session=newsession;
	}
	
	public abstract void newData(ByteBuffer buffer);
	
	public void beep()
	{
		client.sendData((byte)7);
		client.flushBuffer();
	}
	
	public void setSize(int width, int height)
	{
		session.setSize(width,height);
	}
	
	public int getHeight()
	{
		return client.getHeight();
	}
	
	public int getWidth()
	{
		return client.getWidth();
	}
	
	public void close()
	{
		session.close();
	}

	protected abstract void setTextAttributes(ColourInfo attr);
	
	public abstract void setCursorVisibility(boolean vis);
	
	public abstract void clearScreen();
	
	public abstract void clearLine();
	
	public abstract void moveCursor(int row, int column);
	
	public void writeText(ColouredString text)
	{
		Iterator loop = text.iterator();
		StringColourPair pair;
		while (loop.hasNext())
		{
			pair = (StringColourPair)loop.next();
			setTextAttributes(pair.getColour());
			client.sendData(pair.toString().getBytes());
		}
		client.flushBuffer();
	}
	
	public void writeLine(ColouredString text)
	{
		clearLine();
		writeText(text);
	}
	
	public void writeText(String text)
	{
		client.sendData(text.getBytes());
		client.flushBuffer();
	}
	
	public void writeLine(String text)
	{
		clearLine();
		writeText(text);
	}
}
