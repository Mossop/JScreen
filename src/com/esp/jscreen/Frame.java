package com.esp.jscreen;

import com.esp.jscreen.text.ColouredString;

public class Frame extends Container implements Focusable
{
	private Window window;
	protected boolean visible;
	private Rectangle area;
	protected boolean border;
	
	public Frame(Window window)
	{
		super();
		setParent(this);
		this.window=window;
		visible=false;
		area = new Rectangle();
		border=true;
	}
	
	public void show()
	{
		if (!visible)
		{
			window.addFrame(this);
			visible=true;
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
	
	protected ColouredString[] getDisplay(Rectangle area)
	{
		area.translate(-this.area.getLeft(),-this.area.getTop());
		if (border)
		{
			area.translate(-1,-1);
		}
		return null;
	}
	
	public void move(int x, int y)
	{
		area.setOrigin(x,y);
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
	
	Rectangle getArea()
	{
		return area;
	}
	
	protected Frame getFrame()
	{
		return this;
	}
}
