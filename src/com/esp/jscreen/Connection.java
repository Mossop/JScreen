package com.esp.jscreen;

import java.nio.ByteBuffer;
import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.MultiLineBuffer;

public abstract class Connection
{
	/**
		* The current screen width.
		*/
  protected int width;
	/**
		* The current screen height.
		*/
  protected int height;
	/**
		* The current cursor column.
		*/
  protected int cursorcol;
	/**
		* The current cursor row.
		*/
  protected int cursorrow;
	/**
		* The session this object belongs to.
		*/
	protected Session session = null;
	
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
		* Sends a beep to the client.
		*/
	public void beep()
	{
	}
	
	/**
		* Clears the clients screen.
		*/
	public abstract void clearScreen();
	
	/**
		* Sets the desired cursor position on the clients screen.
		*/
	public void setCursorPos(int col, int row)
	{
		cursorcol=col;
		cursorrow=row;
		moveCursor(col,row);
	}
	
	public void writeBlock(int col, int row, MultiLineBuffer lines)
	{
		for (int y=0; y<lines.getLineCount(); y++)
		{
			moveCursor(col,row+y);
			writeText((ColouredString)lines.getLine(y));
		}
		moveCursor(cursorcol,cursorrow);
	}
	
	/**
		* Writes some text at a particular position on the clients screen.
		*/
	public void writeText(int col, int row, ColouredString text)
	{
		moveCursor(col,row);
		writeText(text);
		moveCursor(cursorcol,cursorrow);
	}
	
	/**
		* Writes some text at a particular position on the clients screen.
		*/
	public void writeText(int col, int row, String text)
	{
		moveCursor(col,row);
		writeText(text);
		moveCursor(cursorcol,cursorrow);
	}
	
	/**
		* Used to move the cursor on the clients screen.
		*/
	protected abstract void moveCursor(int col, int row);

	/**
		* Used to write text on the clients screen.
		*/
	protected abstract void writeText(ColouredString text);
	
	/**
		* Used to write text on the clients screen.
		*/
	protected abstract void writeText(String text);
	
	public void setSession(Session newsess)
	{
		session=newsess;
	}
	
	public void processEvent(EventObject event)
	{
		if (session!=null)
		{
			session.processEvent(event);
		}
	}
}
