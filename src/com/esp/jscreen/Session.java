package com.esp.jscreen;

import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.events.ComponentEvent;
import com.esp.jscreen.events.EventHandler;
import com.esp.jscreen.events.KeyEvent;
import com.esp.jscreen.events.KeyListener;
import com.esp.jscreen.events.TerminalEvent;
import com.esp.jscreen.events.TerminalListener;
import com.esp.jscreen.events.ConnectionListener;
import com.esp.jscreen.events.ConnectionEvent;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.text.MultiLineBuffer;
import java.util.List;
import java.util.ArrayList;

public class Session implements KeyListener, TerminalListener, ConnectionListener
{
	private Connection connection;
	private List windows;
	private Window currentwin;
	private Rectangle viewport;
	private List keylisteners;

	public Session(Connection conn)
	{
		connection=conn;
		connection.setSession(this);
		currentwin=null;
		windows = new ArrayList();
		keylisteners = new ArrayList();
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
	
	public void close()
	{
		processEvent(new ConnectionEvent(this,ConnectionEvent.CLOSE));
		connection.close();
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
				if (windows.size()>1)
				{
					oldpos++;
					if (oldpos>=windows.size())
					{
						oldpos=0;
					}
					changeWindow((Window)windows.get(oldpos));
				}
				else
				{
					close();
				}
			}
			windows.remove(oldwin);
		}
		else
		{
			throw new IllegalArgumentException("No such window in this session");
		}
	}
	
	void setCursorPos(Window window, int x, int y)
	{
		if (window==currentwin)
		{
			connection.setCursorPos(x,y);
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
		colour.setBackground(ColourInfo.COLOUR_BLACK);
		colour.setForeground(ColourInfo.COLOUR_WHITE);
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
	
	public void addKeyListener(KeyListener listener)
	{
		keylisteners.add(listener);
	}

	public void removeKeyListener(KeyListener listener)
	{
		keylisteners.remove(listener);
	}
	
	public boolean keyPressed(KeyEvent e)
	{
		boolean used = false;
		int loop=0;
		while ((loop<keylisteners.size())&&(!used))
		{
			used=((KeyListener)keylisteners.get(loop)).keyPressed(e);
			loop++;
		}
		return used;
	}
	
	public boolean terminalResized(TerminalEvent e)
	{
		viewport.setWidth(connection.getWidth());
		viewport.setHeight(connection.getHeight());
		return false;
	}

	public boolean connectionClosed(ConnectionEvent e)
	{
		return false;
	}
	
	public boolean connectionOpened(ConnectionEvent e)
	{
		return false;
	}
	
	protected boolean processEvent(EventObject event)
	{
		if (!EventHandler.channelEvent(this,event))
		{
			if (event instanceof ComponentEvent)
			{
				return currentwin.processEvent(event);
			}
			else
			{
				for (int loop=0; loop<windows.size(); loop++)
				{
					if (((Window)windows.get(loop)).processEvent(event))
					{
						return true;
					}
				}
				return false;
			}
		}
		else
		{
			return true;
		}
	}
	
	protected String toString(String indent)
	{
		StringBuffer result = new StringBuffer();
		result.append("Session: "+super.toString()+"\n");
		for (int loop=0; loop<windows.size(); loop++)
		{
			result.append(((Window)windows.get(loop)).toString("  "));
		}
		return result.toString();
	}
	
	public String toString()
	{
		return toString("");
	}
}
