package com.esp.jscreen;

import com.esp.jscreen.text.MultiLineBuffer;
import com.esp.jscreen.text.ColouredStringBuffer;
import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.events.TerminalEvent;

public class Frame extends Container implements Focusable
{
	private Window window;
	protected boolean visible;
	private Rectangle area;
	protected boolean border;
	private String name;
	
	public Frame(Window window, String name)
	{
		super();
		setParent(this);
		this.window=window;
		visible=false;
		area = new Rectangle();
		border=true;
		this.name=name;
	}
	
	public void show()
	{
		if (!visible)
		{
			System.out.println ("burble!");
			window.addFrame(this);
			visible=true;
		}
	}
	
	public void showCentered()
	{
		if (!visible)
		{
			move((window.getWidth()-getWidth())/2,(window.getHeight()-getHeight())/2);
			show();
		}
	}
	
	public void hide()
	{
		if (visible)
		{
			window.removeFrame(this);
			visible=false;
		}
	}
	
	void updateComponent(Component comp, Rectangle area)
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
			window.updateFrame(this,area);
		}
		else
		{
			throw new IllegalArgumentException("Component that is not part of this container is trying to draw itself");
		}
	}
	
	protected MultiLineBuffer getDisplay(Rectangle area)
	{
		//System.out.println("Redraw of Frame "+getName()+": "+area);
		MultiLineBuffer display = new MultiLineBuffer(area);
		Rectangle container = new Rectangle(this.area);
		if (border)
		{
			if (area.getLeft()==this.area.getLeft())
			{
				ColouredStringBuffer border = new ColouredStringBuffer("|");
				for (int y=0; y<area.getHeight(); y++)
				{
					display.overlay(0,y,border);
				}
			}
			if (area.getRight()==this.area.getRight())
			{
				ColouredStringBuffer border = new ColouredStringBuffer("|");
				for (int y=0; y<area.getHeight(); y++)
				{
					display.overlay(area.getWidth()-1,y,border);
				}
			}
			if (area.getTop()==this.area.getTop())
			{
				ColouredStringBuffer border = new ColouredStringBuffer();
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
			int y=area.getTop()-this.area.getTop();
			int x=area.getLeft()-this.area.getLeft();
			area.translate(-container.getLeft(),-container.getTop());
			display.overlay(x,y,super.getDisplay(area));
		}
		return display;
	}
	
	protected void processEvent(EventObject event)
	{
		super.processEvent(event);
	}
	
	public void move(int x, int y)
	{
		Rectangle oldarea = new Rectangle(area);
		area.setOrigin(x,y);
		if (visible)
		{
			window.frameMoved(this,oldarea,area);
		}
	}
	
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
	
	public String getName()
	{
		return name;
	}
	
	Rectangle getArea()
	{
		return area;
	}
	
	protected Frame getFrame()
	{
		return this;
	}
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append("    Frame: "+getName()+"\n");
		return result.toString();
	}
}
