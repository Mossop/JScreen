package com.esp.jscreen;

import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.events.EventHandler;
import com.esp.jscreen.events.KeyEvent;
import com.esp.jscreen.events.KeyListener;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.text.ColouredStringBuffer;
import com.esp.jscreen.text.MultiLineBuffer;
import java.util.List;
import java.util.ArrayList;

/**
	* The Component is the basic building block of the interface.
	*/
public abstract class Component implements KeyListener
{
	/**
		* The container that this component is held in.
		*/
	private Container parent;
	/**
		* The width of the component.
		*/
	private int width;
	/**
		* The height of the component.
		*/
	private int height;
	/**
		* The keylisteners registered with this component.
		*/
	private List keylisteners;
	
	/**
		* Performs some basic initialisation.
		*/
	public Component()
	{
		parent=null;
		keylisteners = new ArrayList();
	}
	
	/**
		* Called to tell the component what container is holding it.
		* The component automatically removes itself from any container that
		* is already holding it.
		*/
	public void setParent(Container parent)
	{
		if (this.parent!=null)
		{
			this.parent.removeComponent(this);
		}
		this.parent=parent;
	}
	
	/**
		* Returns the container holding the component.
		*/
	protected Container getParent()
	{
		return parent;
	}
	
	/**
		* Focusses the next component. This just returns null so it needs
		* to be overrided for any component that can be focussed.
		*/
	protected Component focusNext()
	{
		return null;
	}
	
	/**
		* Sets the size of the component.
		*/
	public void setSize(int width,int height)
	{
		this.width=width;
		this.height=height;
	}
	
	/**
		* Sets the size of the component.
		*/
	protected void setHeight(int height)
	{
		this.height=height;
	}
	
	/**
		* Sets the size of the component.
		*/
	protected void setWidth(int width)
	{
		this.width=width;
	}
	
	/**
		* Returns the width of the component.
		*/
	public int getWidth()
	{
		return width;
	}
	
	/**
		* Returns the height of the component.
		*/
	public int getHeight()
	{
		return height;
	}
	
	/**
		* Returns the palette that this component is using.
		*/
	public Palette getPalette()
	{
		return parent.getPalette();
	}
	
	/**
		* Returns the minimum height that the component can occupy.
		* The default is 1.
		*/
	public int getMinimumHeight()
	{
		return 1;
	}
	
	/**
		* Returns the minimum width that the component can occupy.
		* The default is 1
		*/
	public int getMinimumWidth()
	{
		return 1;
	}
	
	/**
		* Returns the maximum height that the component can occupy.
		* Return -1 for unlimited height (the default).
		*/
	public int getMaximumHeight()
	{
		return -1;
	}
	
	/**
		* Returns the maximum width that the component can occupy.
		* Return -1 for unlimited width (the default).
		*/
	public int getMaximumWidth()
	{
		return -1;
	}
	
	/**
		* A helper method. Subclasses can call this to get the component updated on screen.
		*/
	protected void update()
	{
		update(new Rectangle(0,0,width,height));
	}
	
	/**
		* Subclasses can call this to get an area of the component updated on screen.
		*/
	protected void update(Rectangle area)
	{
		if (parent!=null)
		{
			parent.updateComponent(this,area);
		}
	}
	
	/**
		* Should return area of the component requested.
		*/
	protected ColouredString getLine(int x, int y, int width)
	{
		return new ColouredStringBuffer("");
	}
	
	/**
		* Should return area of the component requested.
		*/
	protected MultiLineBuffer getDisplay(Rectangle area)
	{
		//System.out.println (getClass().getName()+" "+area);
		MultiLineBuffer lines = new MultiLineBuffer();
		for (int loop=area.getTop(); loop<=area.getBottom(); loop++)
		{
			lines.addLine(getLine(area.getLeft(),loop,area.getWidth()));
		}
		return lines;
	}
	
	/**
		* Registers a keylistener with this component.
		*/
	public void addKeyListener(KeyListener listener)
	{
		keylisteners.add(listener);
	}

	/**
		* Deregisters a keylistener with this component.
		*/
	public void removeKeyListener(KeyListener listener)
	{
		keylisteners.remove(listener);
	}
	
	/**
		* Called when a keypress event has happened.
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
		* Uses the eventhandler to distribute events.
		*/
	protected boolean processEvent(EventObject event)
	{
		return EventHandler.channelEvent(this,event);
	}
	
	protected Frame getFrame()
	{
		if (parent!=null)
		{
			return parent.getFrame();
		}
		else
		{
			return null;
		}
	}
	
	protected Window getWindow()
	{
		if (parent!=null)
		{
			return parent.getWindow();
		}
		else
		{
			return null;
		}
	}
	
	protected Session getSession()
	{
		if (parent!=null)
		{
			return parent.getSession();
		}
		else
		{
			return null;
		}
	}

	/**
		* Returns a debug string
		*/
	protected String toString(String indent)
	{
		return indent+"Component: "+getClass().getName()+"\n";
	}
	
	/**
		* Returns a debug string
		*/
	public String toString()
	{
		return toString("");
	}
}
