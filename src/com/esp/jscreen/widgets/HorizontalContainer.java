package com.esp.jscreen.components;

import com.esp.jscreen.text.ColouredStringBuffer;
import com.esp.jscreen.text.ColouredString;

public class HorizontalContainer extends Container
{
	public HorizontalContainer(Container parent)
	{
		super(parent);
	}
	
	protected void doLayout()
	{
		int remaining = width;
		if (border)
		{
			remaining=remaining-(components.size()-1);
		}
		int norm=remaining/components.size();
		areas.clear();
		int left=0;
		for (int pos=0; pos<components.size(); pos++)
		{
			Component comp = (Component)components.get(pos);
			Area area = new Area();
			area.setTop(0);
			area.setHeight(height);
			if (pos==(components.size()-1))
			{
				area.setWidth(width-left);
			}
			else
			{
				area.setWidth(norm);
			}
			area.setLeft(left);
			left=left+norm;
			if (border)
			{
				left++;
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
		if (border)
		{
			total=total+components.size()-1;
		}
		return total;
	}
	
	public int getMaximumHeight()
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
	
	public int getMaximumWidth()
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
		if (border)
		{
			total=total+components.size()-1;
		}
		return total;
	}

	public ColouredString getLine(int line)
	{
		ColouredStringBuffer buffer = new ColouredStringBuffer();
		for (int loop=0; loop<components.size(); loop++)
		{
			buffer.append(((Component)components.get(loop)).getLine(line));
			if ((border)&&(loop<(components.size()-1)))
			{
				buffer.append("|");
			}
		}
		return buffer;
	}
}
