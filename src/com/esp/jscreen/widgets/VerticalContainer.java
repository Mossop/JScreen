package com.esp.jscreen.components;

import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColouredStringBuffer;

public class VerticalContainer extends Container
{
	public VerticalContainer(Container parent)
	{
		super(parent);
	}
	
	protected void doLayout()
	{
		int remaining = height;
		if (border)
		{
			remaining=remaining-(components.size()-1);
		}
		int norm=remaining/components.size();
		areas.clear();
		int top=0;
		for (int pos=0; pos<components.size(); pos++)
		{
			Component comp = (Component)components.get(pos);
			Area area = new Area();
			area.setLeft(0);
			area.setWidth(width);
			if (pos==(components.size()-1))
			{
				area.setHeight(height-top);
			}
			else
			{
				area.setHeight(norm);
			}
			area.setTop(top);
			top=top+norm;
			if (border)
			{
				top++;
			}
			areas.put(comp,area);
			comp.setSize(area.getWidth(), area.getHeight());
			if (comp instanceof Container)
			{
				((Container)comp).doLayout();
			}
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
		if (border)
		{
			total=total+components.size()-1;
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
		if (border)
		{
			total=total+components.size()-1;
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

	public ColouredString getLine(int line)
	{
		for (int pos=0; pos<components.size(); pos++)
		{
			Component comp = (Component)components.get(pos);
			Area area = (Area)areas.get(comp);
			if ((area.getTop()<=line)&&(area.getTop()+area.getHeight()>line))
			{
				return comp.getLine(line-area.getTop());
			}
			else if ((border)&&(pos<(components.size()-1))&&(area.getTop()+area.getHeight()==line))
			{
				ColouredStringBuffer buffer = new ColouredStringBuffer();
				for (int loop=0; loop<width; loop++)
				{
					buffer.append("-");
				}
				return buffer;
			}
		}
		return null;
	}
}
