package com.esp.jscreen;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.esp.jscreen.text.ColouredString;

public abstract class Container extends Component
{
	protected List components;
	protected Map areas;
	
	public Container()
	{
		components = new ArrayList();
		areas = new HashMap();
	}
	
	protected void layout()
	{
	}
	
	protected void setSize(int width, int height)
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
	
	protected abstract ColouredString[] getDisplay(Rectangle area);
	
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
