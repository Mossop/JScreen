package com.esp.jscreen.widgets;

import com.esp.jscreen.text.ColouredStringBuffer;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.Container;
import com.esp.jscreen.Rectangle;
import com.esp.jscreen.Component;

public class HorizontalContainer extends Container
{
	protected void doLayout()
	{
		if (components.size()>0)
		{
			int remaining = getWidth();
			int norm=remaining/components.size();
			areas.clear();
			int left=0;
			for (int pos=0; pos<components.size(); pos++)
			{
				Component comp = (Component)components.get(pos);
				Rectangle area = new Rectangle();
				area.setTop(0);
				area.setHeight(getHeight());
				if (pos==(components.size()-1))
				{
					area.setWidth(getWidth()-left);
				}
				else
				{
					area.setWidth(norm);
				}
				area.setLeft(left);
				left=left+norm;
				areas.put(comp,area);
				comp.setSize(area.getWidth(), area.getHeight());
			}
		}
	}

	public int getMinimumHeight()
	{
		int min = 0;
		for (int pos=0; pos<components.size(); pos++)
		{
			Component comp = (Component)components.get(pos);
			min=Math.max(min,comp.getMinimumHeight());
		}
		return min;
	}
	
	public int getMinimumWidth()
	{
		int total = 0;
		for (int pos=0; pos<components.size(); pos++)
		{
			Component comp = (Component)components.get(pos);
			total=total+comp.getMinimumWidth();
		}
		return total;
	}
	
	public int getMaximumHeight()
	{
		if (components.size()>0)
		{
			int max = 0;
			for (int pos=0; pos<components.size(); pos++)
			{
				Component comp = (Component)components.get(pos);
				if (comp.getMaximumHeight()>=0)
				{
					max=Math.min(max,comp.getMaximumHeight());
				}
			}
			return max;
		}
		else
		{
			return -1;
		}
	}
	
	public int getMaximumWidth()
	{
		if (components.size()>0)
		{
			int total = 0;
			for (int pos=0; pos<components.size(); pos++)
			{
				Component comp = (Component)components.get(pos);
				if (comp.getMaximumWidth()<0)
				{
					return -1;
				}
				total=total+comp.getMaximumWidth();
			}
			return total;
		}
		else
		{
			return -1;
		}
	}
}
