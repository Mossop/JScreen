package com.esp.jscreen;

import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.text.ColouredStringBuffer;
import com.esp.jscreen.text.MultiLineBuffer;

public abstract class Component
{
	private Container parent;
	private int width;
	private int height;
	
	public Component()
	{
		parent=null;
	}
	
	public void setParent(Container parent)
	{
		if (this.parent!=null)
		{
			this.parent.removeComponent(this);
		}
		this.parent=parent;
	}
	
	protected Container getParent()
	{
		return parent;
	}
	
	public void setSize(int width,int height)
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
	
	public ColourInfo getBackgroundColour()
	{
		return null;
	}
	
	protected void update()
	{
		update(new Rectangle(0,0,width,height));
	}
	
	protected void update(Rectangle area)
	{
		if (parent!=null)
		{
			parent.updateComponent(this,area);
		}
	}
	
	protected ColouredString getLine(int x, int y, int width)
	{
		return new ColouredStringBuffer("");
	}
	
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
	
	protected void processEvent(EventObject event)
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

	protected String toString(String indent)
	{
		return indent+"Component: "+getClass().getName()+"\n";
	}
	
	public String toString()
	{
		return toString("");
	}
}
