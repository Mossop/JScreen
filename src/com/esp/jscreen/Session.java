package com.esp.jscreen;

import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.text.ColouredString;
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
		currentwin=null;
		windows = new ArrayList();
		viewport = new Rectangle(0,0,80,25);
	}
	
	void addWindow(Window newwin)
	{
		windows.add(newwin);
		changeWindow(newwin);
	}
	
	public void changeWindow(Window newwin)
	{
		int newpos = windows.indexOf(newwin);
		if (newpos>=0)
		{
			currentwin=newwin;
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
	
	private void updateLine(int x, int y, ColouredString text)
	{
		connection.writeText(x,y,text);
	}
	
	void updateDisplay(Window window, int x, int y, ColouredString line)
	{
		if (window==currentwin)
		{
			updateLine(x-viewport.getLeft(),y-viewport.getRight(),line);
		}
	}
	
	void updateDisplay(Window window, Rectangle rect, ColouredString[] lines)
	{
		if (window==currentwin)
		{
			for (int y=0; y<=rect.getHeight(); y++)
			{
				updateLine(rect.getLeft()-viewport.getLeft(),y+rect.getTop()-viewport.getTop(),lines[y]);
			}
		}
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
	
	public void processEvent(EventObject event)
	{
	}
}
