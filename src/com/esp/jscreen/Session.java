package com.esp.jscreen;

import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.events.TerminalEvent;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.text.MultiLineBuffer;
import java.util.List;
import java.util.ArrayList;

public class Session
{
	private Connection connection;
	private List windows;
	private Window currentwin;
	private Rectangle viewport;
	
	public Session(Connection conn)
	{
		connection=conn;
		connection.setSession(this);
		currentwin=null;
		windows = new ArrayList();
		viewport = new Rectangle(0,0,80,25);
	}
	
	void addWindow(Window newwin)
	{
		windows.add(newwin);
		changeWindow(newwin);
	}
	
	public void redraw()
	{
		//connection.clearScreen();
		updateDisplay(currentwin,viewport,currentwin.getWindow(viewport));
	}
	
	public void changeWindow(Window newwin)
	{
		int newpos = windows.indexOf(newwin);
		if (newpos>=0)
		{
			currentwin=newwin;
			redraw();
		}
		else
		{
			throw new IllegalArgumentException("No such window in this session");
		}
	}
	
	void removeWindow(Window oldwin)
	{
		int oldpos = windows.indexOf(oldwin);
		if (oldpos>=0)
		{
			if (oldwin==currentwin)
			{
				oldpos++;
				if (oldpos>=windows.size())
				{
					oldpos=0;
				}
				changeWindow((Window)windows.get(oldpos));
			}
			windows.remove(oldwin);
		}
		else
		{
			throw new IllegalArgumentException("No such window in this session");
		}
	}
	
	void updateDisplay(Window window, int x, int y, ColouredString line)
	{
		if (window==currentwin)
		{
			connection.writeText(x-viewport.getLeft(),y-viewport.getRight(),line);
		}
	}
	
	void updateDisplay(Window window, Rectangle rect, MultiLineBuffer lines)
	{
		if (window==currentwin)
		{
			connection.writeBlock(rect.getLeft()-viewport.getLeft(),rect.getTop()-viewport.getTop(),lines);
		}
	}

	ColourInfo getWindowBackgroundColour()
	{
		ColourInfo colour = new ColourInfo();
		colour.setBackground(ColourInfo.COLOUR_WHITE);
		colour.setForeground(ColourInfo.COLOUR_BLACK);
		return colour;
	}
	
	ColourInfo getContainerBackgroundColour()
	{
		return getFrameBackgroundColour();
	}
	
	ColourInfo getFrameBackgroundColour()
	{
		ColourInfo colour = new ColourInfo();
		colour.setBackground(ColourInfo.COLOUR_BLUE);
		colour.setForeground(ColourInfo.COLOUR_YELLOW);
		colour.setBold(true);
		return colour;
	}
	
	public int getWidth()
	{
		return viewport.getWidth();
	}
	
	public int getHeight()
	{
		return viewport.getHeight();
	}
	
	public Rectangle getViewPort()
	{
		return new Rectangle(viewport);
	}
	
	private void sendWindowEvent(EventObject event)
	{
		for (int loop=0; loop<windows.size(); loop++)
		{
			((Window)windows.get(loop)).processEvent(event);
		}
	}
	
	protected void processEvent(EventObject event)
	{
		if (event instanceof TerminalEvent)
		{
			TerminalEvent termev = (TerminalEvent)event;
			switch (termev.getEvent())
			{
				case TerminalEvent.RESIZE:	viewport.setWidth(connection.getWidth());
																		viewport.setHeight(connection.getHeight());
																		sendWindowEvent(event);
																		redraw();
																		break;
			}
		}
	}
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append("Session: "+super.toString()+"\n");
		for (int loop=0; loop<windows.size(); loop++)
		{
			result.append(windows.get(loop).toString());
		}
		return result.toString();
	}
}
