package com.esp.jscreen;

import com.esp.jscreen.text.ColouredString;

public class Frame extends Container implements Focusable
{
	private Window window;
	protected boolean visible;
	private Rectangle area;
	
	public Frame(Window window)
	{
		super();
		setParent(this);
		this.window=window;
		visible=false;
		area = new Rectangle();
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
		return null;
	}
	
	public void move(int x, int y)
	{
		area.setOrigin(x,y);
	}
	
	public void setSize(int width, int height)
	{
		area.setSize(width,height);
		super.setSize(width,height);
	}
	
	public void setHeight(int height)
	{
		area.setHeight(height);
		super.setHeight(height);
	}
	
	public void setWidth(int width)
	{
		area.setWidth(width);
		super.setWidth(width);
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
