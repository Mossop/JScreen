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

/**
 * The session manages multiple Windows for the client to view.
 */
public class Session implements KeyListener, TerminalListener, ConnectionListener
{
	/**
	 * The Connection to the client.
	 */
	private Connection connection;
	/**
	 * The windows that the client can view in their z-order
	 */
	private List windows;
	/**
	 * A quick access to the current window or null if there are no windows.
	 */
	private Window currentwin;
	/**
	 * Holds the window size. Note that this can be larger or smaller than the clients
	 * window size.
	 */
	private Rectangle viewport;
	/**
	 * KeyListeners that have registered for events from the Session
	 */
	private List keylisteners;

	/**
	 * Sets up the session with the given Connection
	 * Sets the viewport to a default of 80x25.
	 */
	public Session(Connection conn)
	{
		connection=conn;
		connection.setSession(this);
		currentwin=null;
		windows = new ArrayList();
		keylisteners = new ArrayList();
		viewport = new Rectangle(0,0,80,25);
	}

	/**
	 * Adds a new window to the list and changes to view it.
	 */
	void addWindow(Window newwin)
	{
		windows.add(newwin);
		changeWindow(newwin);
	}

	/**
	 * Redraws the display
	 */
	public void redraw()
	{
		//connection.clearScreen();
		updateDisplay(currentwin,viewport,currentwin.getWindow(viewport));
	}

	/**
	 * Called to end the connection to the client.
	 * Signals a close event to everything, then tells the connection itself to close.
	 */
	public void close()
	{
		processEvent(new ConnectionEvent(this,ConnectionEvent.CLOSE));
		connection.close();
	}

	/**
	 * Changes to the given window and redraws the screen.
	 */
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

	/**
	 * Removes the given window from the Session.
	 * If the window is displayed, then the next window is viewed. If no windows are
	 * left then close() is called to end the connection.
	 */
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

	/**
	 * Moves the onscreen cursor to the gived coordinates for the given window.
	 */
	void setCursorPos(Window window, int x, int y)
	{
		if (window==currentwin)
		{
			connection.setCursorPos(x,y);
		}
	}

	/**
	 * Called by the Window usually to say it has changed.
	 * Passes the window that changed, the x,y of the start position and the new text.
	 */
	void updateDisplay(Window window, int x, int y, ColouredString line)
	{
		if (window==currentwin)
		{
			connection.writeText(x-viewport.getLeft(),y-viewport.getRight(),line);
		}
	}

	/**
	 * Called by the Window usually to say it has changed.
	 * Passes the window that changed, the area of the change and the new text.
	 */
	void updateDisplay(Window window, Rectangle rect, MultiLineBuffer lines)
	{
		if (window==currentwin)
		{
			connection.writeBlock(rect.getLeft()-viewport.getLeft(),rect.getTop()-viewport.getTop(),lines);
		}
	}

	/**
	 * Returns the current default palettefor windows.
	 */
	Palette getWindowPalette()
	{
		Palette palette = new Palette();
		ColourInfo colour = new ColourInfo();
		colour.setBackground(ColourInfo.COLOUR_BLACK);
		colour.setForeground(ColourInfo.COLOUR_WHITE);
		colour.setBold(true);
		palette.setColour("BORDER",colour);
		colour = new ColourInfo();
		colour.setBackground(ColourInfo.COLOUR_BLACK);
		colour.setForeground(ColourInfo.COLOUR_WHITE);
		palette.setColour("FRAME",colour);
		return palette;
	}

	/**
	 * Returns the current default palette for subdialogs.
	 */
	Palette getDialogPalette()
	{
		Palette palette = new Palette();
		ColourInfo colour = new ColourInfo();
		colour.setBackground(ColourInfo.COLOUR_BLUE);
		colour.setForeground(ColourInfo.COLOUR_YELLOW);
		colour.setBold(true);
		palette.setColour("BORDER",colour);
		colour = new ColourInfo();
		colour.setBackground(ColourInfo.COLOUR_BLUE);
		colour.setForeground(ColourInfo.COLOUR_YELLOW);
		palette.setColour("FRAME",colour);
		return palette;
	}

	/**
	 * returns the width of the viewport.
	 */
	public int getWidth()
	{
		return viewport.getWidth();
	}

	/**
	 * returns the height of the viewport.
	 */
	public int getHeight()
	{
		return viewport.getHeight();
	}

	/**
	 * returns a copy of the viewport rectangle.
	 */
	public Rectangle getViewPort()
	{
		return new Rectangle(viewport);
	}

	/**
	 * Registers a new keylistener with the session.
	 */
	public void addKeyListener(KeyListener listener)
	{
		keylisteners.add(listener);
	}

	/**
	 * Deregisters a keylistener with the session.
	 */
	public void removeKeyListener(KeyListener listener)
	{
		keylisteners.remove(listener);
	}

	/**
	 * called when a key has been pressed. Simply passes it onto the registered keylisteners.
	 */
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

	/**
	 * This will get called when the connection signales that the clients screen
	 * size has changed. Simply updates the viewport as necessary for now.
	 */
	public boolean terminalResized(TerminalEvent e)
	{
		viewport.setWidth(connection.getWidth());
		viewport.setHeight(connection.getHeight());
		return false;
	}

	/**
	 * Called when the connection closes. Does nothing
	 */
	public boolean connectionClosed(ConnectionEvent e)
	{
		return false;
	}

	/**
	 * Called when the connection opens?
	 */
	public boolean connectionOpened(ConnectionEvent e)
	{
		return false;
	}

	/**
	 * Calls the eventhandler to process the event.
	 * If this does not cause the event to be handled, then we look at the event.
	 * If it is a ComponentEvent then it must be for the current window, so we pass it on.
	 * Otherwise we pass it to all the windows in turn until one handles it.
	 */
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

	/**
	 * returns a debug string to display info on the session.
	 * The string will be indented by thegiven amount.
	 */
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

	/**
	 * Returns the debug string for this session.
	 */
	public String toString()
	{
		return toString("");
	}
}
