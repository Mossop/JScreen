package com.esp.jscreen;

import com.esp.jscreen.text.MultiLineBuffer;
import com.esp.jscreen.text.ColouredStringBuffer;
import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.events.TerminalEvent;
import com.esp.jscreen.widgets.VerticalContainer;
import com.esp.jscreen.events.FocusListener;
import com.esp.jscreen.events.FocusEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * The Frame acts as a dialog box.
 */
public class Frame extends VerticalContainer implements Focusable
{
	/**
	 * The frames window.
	 */
	private Window window;
	/**
	 * Tells whether the frame is visible (but possible obscured) or not.
	 */
	protected boolean visible;
	/**
	 * The area that the frame occupies.
	 */
	private Rectangle area;
	/**
	 * Whether the draw a border on the frame.
	 */
	protected boolean border;
	/**
	 * The name of the frame.
	 */
	private String name;
	/**
	 * Any registered focuslisteners
	 */
	private List focuslisteners;
	/**
	 * The currently focussed component???
	 */
	private Component targetcomp;

	/**
	 * Initialises the frame.
	 */
	public Frame(Window window, String name)
	{
		super();
		setParent(this);
		targetcomp=null;
		focuslisteners = new ArrayList();
		this.window=window;
		visible=false;
		area = new Rectangle();
		border=true;
		this.name=name;
	}

	/**
	 * Registers the frame with the window if necessary and moves it on top.
	 */
	public void show()
	{
		if (!visible)
		{
			if (!isValidated())
			{
				layout();
			}
			focusNext();
			window.addFrame(this);
			visible=true;
		}
	}

	/**
	 * Shows the frame in the middle of the screen.
	 */
	public void showCentered()
	{
		if (!visible)
		{
			move((window.getWidth()-getWidth())/2,(window.getHeight()-getHeight())/2);
			show();
		}
	}

	/**
	 * Removes the frame from the window
	 */
	public void hide()
	{
		if (visible)
		{
			window.removeFrame(this);
			visible=false;
		}
	}

	/**
	 * Called by a component to move the cursor to some new place.
	 */
	public void moveCursor(Component comp, int x, int y)
	{
		if (getWindow()!=null)
		{
			if (comp==focussed)
			{
				Rectangle comparea = (Rectangle)areas.get(comp);
				x=x+comparea.getLeft();
				y=y+comparea.getTop();
				if (border)
				{
					y=y+1;
					x=x+1;
				}
				getWindow().setCursorPos(this,x,y);
			}
			else
			{
				throw new IllegalArgumentException("Frame that is not focussed is trying to move the cursor");
			}
		}
	}

	/**
	 * Tells the frame that a component has updated.
	 */
	void updateComponent(Component comp, Rectangle area)
	{
		if (getWindow()!=null)
		{
			if (components.indexOf(comp)>=0)
			{
				Rectangle comparea = (Rectangle)areas.get(comp);
				area.translate(comparea.getLeft(),comparea.getTop());
				area.translate(this.area.getLeft(),this.area.getTop());
				if (border)
				{
					area.translate(1,1);
				}
				getWindow().updateFrame(this,area);
			}
			else
			{
				throw new IllegalArgumentException("Component that is not part of this container is trying to draw itself");
			}
		}
	}

	/**
	 * Returns a buffer of the display of the frame.
	 */
	protected MultiLineBuffer getDisplay(Rectangle area)
	{
		//System.out.println (getClass().getName()+" "+area);
		MultiLineBuffer display = new MultiLineBuffer(getBackgroundColour(),area);
		Rectangle container = new Rectangle(this.area);
		if (border)
		{
			if (area.getLeft()==this.area.getLeft())
			{
				ColouredStringBuffer border = new ColouredStringBuffer("|");
				border.setColourAt(0,getBackgroundColour());
				for (int y=0; y<area.getHeight(); y++)
				{
					display.overlay(0,y,border);
				}
			}
			if (area.getRight()==this.area.getRight())
			{
				ColouredStringBuffer border = new ColouredStringBuffer("|");
				border.setColourAt(0,getBackgroundColour());
				for (int y=0; y<area.getHeight(); y++)
				{
					display.overlay(area.getWidth()-1,y,border);
				}
			}
			if (area.getTop()==this.area.getTop())
			{
				ColouredStringBuffer border = new ColouredStringBuffer();
				border.setColourAt(0,getBackgroundColour());
				for (int loop=0; loop<area.getWidth(); loop++)
				{
					if (((area.getLeft()+loop)==this.area.getLeft())||((area.getLeft()+loop)==this.area.getRight()))
					{
						border.append("+");
					}
					else
					{
						border.append("-");
					}
				}
				display.overlay(0,0,border);
			}
			if (area.getBottom()==this.area.getBottom())
			{
				ColouredStringBuffer border = new ColouredStringBuffer();
				border.setColourAt(0,getBackgroundColour());
				for (int loop=0; loop<area.getWidth(); loop++)
				{
					if (((area.getLeft()+loop)==this.area.getLeft())||((area.getLeft()+loop)==this.area.getRight()))
					{
						border.append("+");
					}
					else
					{
						border.append("-");
					}
				}
				display.overlay(0,area.getHeight()-1,border);
			}
			container.translate(1,1);
			container.setSize(container.getWidth()-2,container.getHeight()-2);
			area=area.union(container);
		}
		if (area.getArea()>0)
		{
			area.translate(-container.getLeft(),-container.getTop());
			display.overlay(container.getLeft(),container.getTop(),super.getDisplay(area));
		}
		return display;
	}

	/**
	 * Gets the background colour for the frame. uses the crude palette in the session.
	 */
	public ColourInfo getBackgroundColour()
	{
		ColourInfo colour = super.getBackgroundColour();
		if (colour==null)
		{
			return getSession().getFrameBackgroundColour();
		}
		else
		{
			return colour;
		}
	}

	/**
	 * Tries to focus the next component.
	 */
	public Component focusNext()
	{
		Component next = super.focusNext();
		processEvent(new FocusEvent(this,targetcomp,FocusEvent.FOCUS_LOST,next));
		processEvent(new FocusEvent(this,next,FocusEvent.FOCUS_GAINED,targetcomp));
		targetcomp=next;
		return next;
	}

	/**
	 * Registers a focus listener.
	 */
	public void addFocusListener(FocusListener listener)
	{
		focuslisteners.add(listener);
	}

	/**
	 * Deregisters a focus listener.
	 */
	public void removeFocusListener(FocusListener listener)
	{
		focuslisteners.remove(listener);
	}

	/**
	 * Tells the frame it has lost focus.
	 */
	public boolean focusLost(FocusEvent e)
	{
		boolean used = false;
		int loop=0;
		while ((loop<focuslisteners.size())&&(!used))
		{
			used=((FocusListener)focuslisteners.get(loop)).focusLost(e);
			loop++;
		}
		return used;
	}

	/**
	 * Tells the frame it has gained focus.
	 */
	public boolean focusGained(FocusEvent e)
	{
		boolean used = false;
		int loop=0;
		while ((loop<focuslisteners.size())&&(!used))
		{
			used=((FocusListener)focuslisteners.get(loop)).focusGained(e);
			loop++;
		}
		return used;
	}

	/**
	 * Process events
	 */
	protected boolean processEvent(EventObject event)
	{
		return super.processEvent(event);
	}

	/**
	 * Moves the frame on the window.
	 */
	public void move(int x, int y)
	{
		Rectangle oldarea = new Rectangle(area);
		area.setOrigin(x,y);
		if (visible)
		{
			window.frameMoved(this,oldarea,area);
		}
	}

	/**
	 * Resizes the frame.
	 */
	public void setSize(int width, int height)
	{
		area.setSize(width,height);
		if (border)
		{
			super.setSize(width-1,height-2);
		}
		else
		{
			super.setSize(width,height);
		}
	}

	/**
	 * Resizes the frame.
	 */
	public void setHeight(int height)
	{
		area.setHeight(height);
		if (border)
		{
			super.setHeight(height-2);
		}
		else
		{
			super.setHeight(height);
		}
	}

	/**
	 * Resizes the frame.
	 */
	public void setWidth(int width)
	{
		area.setWidth(width);
		if (border)
		{
			super.setWidth(width-2);
		}
		else
		{
			super.setWidth(width);
		}
	}

	/**
	 * Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the frames area.
	 */
	Rectangle getArea()
	{
		return area;
	}

	protected Frame getFrame()
	{
		return this;
	}

	protected Window getWindow()
	{
		return window.getWindow();
	}

	protected Session getSession()
	{
		return window.getSession();
	}

	/**
	 * Returns a debug string
	 */
	protected String toString(String indent)
	{
		StringBuffer result = new StringBuffer();
		result.append(indent+"Frame: "+getName()+"\n");
		result.append(super.toString(indent+"  "));
		return result.toString();
	}
}
