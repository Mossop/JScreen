package com.esp.jscreen;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.esp.jscreen.text.MultiLineBuffer;
import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.events.TerminalEvent;
import com.esp.jscreen.events.TerminalListener;
import com.esp.jscreen.events.EventHandler;
import com.esp.jscreen.events.ComponentEvent;

/**
 * The window is a sinple frame that the client can see.
 * It also holds a collection of subframes that get overlaid on the main frame.
 */
public class Window extends Frame implements TerminalListener
{
	/**
	 * The session.
	 */
	private Session session;
	/**
	 * The subframes or "dialogs"
	 */
	private List subframes;
	/**
	 * The current frame thats on top.
	 */
	private Frame currentframe;

	/**
	 * Initialises stuff.
	 * Every frame (and hence window) has a name for display.
	 * Sets the currentframe to this.
	 */
	public Window(Session session, String name)
	{
		super(null,name);
		this.session=session;
		subframes = new ArrayList();
		currentframe=this;
		border=false;
	}

	/**
	 * Adds a new frame and displays it.
	 */
	void addFrame(Frame newframe)
	{
		subframes.add(newframe);
		changeFrame(newframe);
	}

	/**
	 * Displays a different frame.
	 */
	private void changeFrame(Frame newframe)
	{
		currentframe=newframe;
	}

	/**
	 * Removes a frame (you cannot remove the main frame.
	 */
	void removeFrame(Frame oldframe)
	{
		int index = subframes.indexOf(oldframe);
		if (index>=0)
		{
			if (oldframe==currentframe)
			{
				index--;
				if (index<0)
				{
					changeFrame(this);
				}
				else
				{
					changeFrame((Frame)subframes.get(index-1));
				}
			}
			subframes.remove(oldframe);
		}
		else
		{
			throw new IllegalArgumentException("No such frame in this window");
		}
	}

	/**
	 * Tells the window to show itself to the client.
	 */
	public void show()
	{
		if (visible)
		{
			session.changeWindow(this);
		}
		else
		{
			if (!isValidated())
			{
				layout();
			}
			focusNext();
			session.addWindow(this);
			visible=true;
		}
	}

	/**
	 * Tells the window to hide itself from the client.
	 */
	public void hide()
	{
		session.removeWindow(this);
		visible=false;
	}

	/**
	 * Returns a buffer of the given area of the window.
	 */
	MultiLineBuffer getWindow(Rectangle viewport)
	{
		MultiLineBuffer buffer = getDisplay(viewport.union(getArea()));
		for (int loop=0; loop<subframes.size(); loop++)
		{
			Frame thisframe = (Frame)subframes.get(loop);
			Rectangle wanted = viewport.union(thisframe.getArea());
			if (wanted.getArea()>0)
			{
				buffer.overlay(wanted.getLeft()-viewport.getLeft(),wanted.getTop()-viewport.getTop(),thisframe.getDisplay(wanted));
			}
		}
		return buffer;
	}

	/**
	 * Called by a frame to indicate that it has moved on the window.
	 * For the moment just redraws the entire screen.
	 */
	void frameMoved(Frame frame, Rectangle oldarea, Rectangle area)
	{
		session.redraw();
	}

	/**
	 * Called by a frame telling the window where it wants the cursor to appear.
	 */
	void setCursorPos(Frame frame, int x, int y)
	{
		if (visible)
		{
			if (frame==currentframe)
			{
				Rectangle comparea = frame.getArea();
				x=x+comparea.getLeft();
				y=y+comparea.getTop();
				session.setCursorPos(this,x,y);
			}
			else
			{
				throw new IllegalArgumentException("Frame that is not focussed is trying to move the cursor");
			}
		}
	}

	/**
	 * Called by a Frame to indicate some of it has updated.
	 * Does some extensive work to remove areas of the frame covered so only shown bits
	 * are redrawn.
	 */
	void updateFrame(Frame frame, Rectangle area)
	{
		if (visible)
		{
			int z = subframes.indexOf(frame);
			if ((z>=0)||(frame==this))
			{
				area=area.union(session.getViewPort()).union(frame.getArea());
				if (area.getArea()>0)
				{
					List areas = new ArrayList();
					List nextareas;
					Rectangle framearea;
					areas.add(area);
					for (int loop=subframes.size()-1; ((loop>z)&&(areas.size()>0)); loop--)
					{
						nextareas = new ArrayList();
						framearea=((Frame)subframes.get(loop)).getArea();
						for (int subloop=0; subloop<areas.size(); subloop++)
						{
							Rectangle thisrect = (Rectangle)areas.get(subloop);
							nextareas.addAll(Arrays.asList(thisrect.subtract(framearea)));
						}
						areas=nextareas;
					}
					for (int loop=0; loop<areas.size(); loop++)
					{
						Rectangle thisrect = (Rectangle)areas.get(loop);
						MultiLineBuffer lines = frame.getDisplay(thisrect);
						session.updateDisplay(this,thisrect,lines);
					}
				}
			}
			else
			{
				throw new IllegalArgumentException("No such frame in this window");
			}
		}
	}

	/**
	 * Gets the background colour.
	 * This refers to the crap pallete arrangement in Session right now.
	 */
	public ColourInfo getBackgroundColour()
	{
		return getSession().getWindowBackgroundColour();
	}

	/**
	 * This would normally move a frame, but as its a window it throws an exception!
	 */
	public void move(int x, int y)
	{
		throw new IllegalArgumentException("You cant move the window");
	}

	/**
	 * This would normally resize a frame, but as its a window it throws an exception!
	 */
	public void setSize(int width, int height)
	{
		throw new IllegalArgumentException("You cant change the size of the window");
	}

	/**
	 * This would normally move a frame, but as its a window it throws an exception!
	 */
	public void setHeight(int height)
	{
		throw new IllegalArgumentException("You cant change the size of the window");
	}

	/**
	 * This would normally move a frame, but as its a window it throws an exception!
	 */
	public void setWidth(int width)
	{
		throw new IllegalArgumentException("You cant change the size of the window");
	}

	/**
	 * Returns the windows width.
	 * Refers to the session.
	 */
	public int getWidth()
	{
		return session.getWidth();
	}

	/**
	 * Returns the windows height.
	 * Refers to the session.
	 */
	public int getHeight()
	{
		return session.getHeight();
	}

	protected Window getWindow()
	{
		return this;
	}

	/**
	 * Returns the session.
	 */
	protected Session getSession()
	{
		return session;
	}

	/**
	 * Called when the clients screen size changes.
	 */
	public boolean terminalResized(TerminalEvent e)
	{
		super.setSize(getWidth(),getHeight());
		return false;
	}

	/**
	 * Processes events using the eventhandler.
	 * Passes unhandled componentevents onto the current frame.
	 * Passes and other unhandled events onto all frames.
	 */
	protected boolean processEvent(EventObject event)
	{
		if (!EventHandler.channelEvent(this,event))
		{
			if (event instanceof ComponentEvent)
			{
				if (currentframe==this)
				{
					return super.processEvent(event);
				}
				else
				{
					return currentframe.processEvent(event);
				}
			}
			else
			{
				if (!super.processEvent(event))
				{
					for (int loop=0; loop<subframes.size(); loop++)
					{
						if (((Frame)subframes.get(loop)).processEvent(event))
						{
							return true;
						}
					}
					return false;
				}
				else
				{
					return true;
				}
			}
		}
		else
		{
			return true;
		}
	}

	/**
	 * Returns a debug string.
	 */
	protected String toString(String indent)
	{
		StringBuffer result = new StringBuffer();
		result.append(indent+"Window: "+getName()+"\n");
		result.append(super.toString(indent+"  "));
		for (int loop=0; loop<subframes.size(); loop++)
		{
			result.append(((Frame)subframes.get(loop)).toString(indent+"  "));
		}
		return result.toString();
	}
}
