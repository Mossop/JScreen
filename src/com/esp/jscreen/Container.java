package com.esp.jscreen;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.esp.jscreen.text.MultiLineBuffer;
import com.esp.jscreen.text.ColourInfo;

public abstract class Container extends Component
{
	protected List components;
	protected Map areas;
	
	public Container()
	{
		components = new ArrayList();
		areas = new HashMap();
	}
	
	protected abstract void layout();
	
	public void setSize(int width, int height)
	{
		super.setSize(width,height);
		layout();
	}
	
	protected void setHeight(int height)
	{
		super.setHeight(height);
		layout();
	}
	
	protected void setWidth(int width)
	{
		super.setWidth(width);
		layout();
	}
	
	public void addComponent(Component newc)
	{
		components.add(newc);
		newc.setParent(this);
		layout();
	}
	
	public void removeComponent(Component newc)
	{
		components.remove(newc);
		newc.setParent(null);
		layout();
	}
	
	protected MultiLineBuffer getDisplay(Rectangle area)
	{
		//System.out.println("Redraw of Container: "+area);
		MultiLineBuffer display = new MultiLineBuffer(getBackgroundColour(),area);
		for (int loop=0; loop<components.size(); loop++)
		{
			Component thisc = (Component)components.get(loop);
			Rectangle carea = (Rectangle)areas.get(thisc);
			Rectangle drawarea = area.union(carea);
			if (drawarea.getArea()>0)
			{
				int x=drawarea.getLeft();
				int y=drawarea.getTop();
				drawarea.translate(-carea.getLeft(),-carea.getTop());
				display.overlay(x,y,thisc.getDisplay(drawarea));
			}
		}
		return display;
	}
	
	public ColourInfo getBackgroundColour()
	{
		ColourInfo colour = super.getBackgroundColour();
		if (colour==null)
		{
			return getSession().getContainerBackgroundColour();
		}
		else
		{
			return colour;
		}
	}
	
	void updateComponent(Component comp, Rectangle area)
	{
		if (components.indexOf(comp)>=0)
		{
			Rectangle comparea = (Rectangle)areas.get(comp);
			area.translate(comparea.getLeft(),comparea.getTop());
			getParent().updateComponent(this,area);
		}
		else
		{
			throw new IllegalArgumentException("Component that is not part of this container is trying to draw itself");
		}
	}
}
