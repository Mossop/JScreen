package com.esp.jscreen;

import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColouredStringBuffer;

public abstract class Component
{
	private Container parent;
	private int width;
	private int height;
	
	public Component()
	{
		parent=null;
	}
	
	void setParent(Container parent)
	{
		if (parent!=null)
		{
			parent.removeComponent(this);
		}
		this.parent=parent;
	}
	
	protected Container getParent()
	{
		return parent;
	}
	
	protected void setSize(int width,int height)
	{
		this.width=width;
		this.height=height;
	}
	
	protected void setHeight(int height)
	{
		this.height=height;
	}
	
	protected void setWidth(int width)
	{
		this.width=width;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getMinimumHeight()
	{
		return 1;
	}
	
	public int getMinimumWidth()
	{
		return 1;
	}
	
	public int getMaximumHeight()
	{
		return -1;
	}
	
	public int getMaximumWidth()
	{
		return -1;
	}
	
	protected ColouredString getLine(int x, int y, int width)
	{
		return null;
	}
	
	protected ColouredString[] getDisplay(Rectangle area)
	{
		ColouredString[] lines = new ColouredString[area.getHeight()];
		for (int loop=area.getTop(); loop<=area.getBottom(); loop++)
		{
			lines[loop=area.getTop()]=getLine(area.getLeft(),loop,area.getWidth());
		}
		return lines;
	}
	
	public void processEvent(EventObject event)
	{
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
}
