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

public abstract class Component implements KeyListener
{
	private Container parent;
	private int width;
	private int height;
	private ColourInfo background;
	private List keylisteners;
	
	public Component()
	{
		parent=null;
		background=null;
		keylisteners = new ArrayList();
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
	
	protected Component focusNext()
	{
		return null;
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
	
	public void setBackgroundColour(ColourInfo colour)
	{
		background=colour;
	}
	
	public ColourInfo getBackgroundColour()
	{
		if (background!=null)
		{
			return background;
		}
		else
		{
			if (parent!=null)
			{
				return parent.getBackgroundColour();
			}
			else
			{
				return null;
			}
		}
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

	protected String toString(String indent)
	{
		return indent+"Component: "+getClass().getName()+"\n";
	}
	
	public String toString()
	{
		return toString("");
	}
}
