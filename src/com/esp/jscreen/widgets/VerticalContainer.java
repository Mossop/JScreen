package com.esp.jscreen.widgets;

import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColouredStringBuffer;
import com.esp.jscreen.Container;
import com.esp.jscreen.Rectangle;
import com.esp.jscreen.Component;

public class VerticalContainer extends Container
{	
	protected void layout()
	{
		int remaining = getHeight();
		int norm=remaining/components.size();
		areas.clear();
		int top=0;
		for (int pos=0; pos<components.size(); pos++)
		{
			Component comp = (Component)components.get(pos);
			Rectangle area = new Rectangle();
			area.setLeft(0);
			area.setWidth(getWidth());
			if (pos==(components.size()-1))
			{
				area.setHeight(getHeight()-top);
			}
			else
			{
				area.setHeight(norm);
			}
			area.setTop(top);
			top=top+norm;
			areas.put(comp,area);
			comp.setSize(area.getWidth(), area.getHeight());
		}
	}

	public int getMinimumHeight()
	{
		int total = 0;
		for (int pos=0; pos<components.size(); pos++)
		{
			Component comp = (Component)components.get(pos);
			total=total+comp.getMinimumHeight();
		}
		return total;
	}
	
	public int getMinimumWidth()
	{
		int min = 0;
		for (int pos=0; pos<components.size(); pos++)
		{
			Component comp = (Component)components.get(pos);
			min=Math.max(min,comp.getMinimumWidth());
		}
		return min;
	}
	
	public int getMaximumHeight()
	{
		int total = 0;
		for (int pos=0; pos<components.size(); pos++)
		{
			Component comp = (Component)components.get(pos);
			if (comp.getMaximumHeight()<0)
			{
				return -1;
			}
			total=total+comp.getMaximumHeight();
		}
		return total;
	}
	
	public int getMaximumWidth()
	{
		int max = 0;
		for (int pos=0; pos<components.size(); pos++)
		{
			Component comp = (Component)components.get(pos);
			if (comp.getMaximumWidth()>=0)
			{
				max=Math.min(max,comp.getMaximumWidth());
			}
		}
		return max;
	}
}
