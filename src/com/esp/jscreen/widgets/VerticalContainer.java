package com.esp.jscreen.widgets;

import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColouredStringBuffer;
import com.esp.jscreen.Container;
import com.esp.jscreen.Rectangle;
import com.esp.jscreen.Component;

public class VerticalContainer extends Container
{	
	protected void doLayout()
	{
		if (components.size()>0)
		{
			int busy = 0;
			for (int loop=0; loop<components.size(); loop++)
			{
				Component thisc = (Component)components.get(loop);
				Rectangle thisa = (Rectangle)areas.get(thisc);
				thisa.setLeft(0);
				thisa.setHeight(thisc.getMinimumHeight());
				if ((thisa.getHeight()<thisc.getMaximumHeight())||(thisc.getMaximumHeight()==-1))
				{
					busy++;
				}
			}
			int remaining = getHeight()-getMinimumHeight();
			int extra;
			int loop;
			while ((busy>0)&&(remaining>0))
			{
				extra=remaining/busy;
				if (extra==0)
				{
					extra=1;
				}
				busy=0;
				loop=0;
				while ((remaining>0)&&(loop<components.size()))
				{
					Component thisc = (Component)components.get(loop);
					Rectangle newa = (Rectangle)areas.get(thisc);
					int maxh = thisc.getMaximumHeight();
					if (maxh<0)
					{
						newa.setHeight(newa.getHeight()+extra);
						remaining=remaining-extra;
						busy++;
					}
					else
					{
						if (newa.getHeight()<maxh)
						{
							newa.setHeight(newa.getHeight()+extra);
							remaining=remaining-extra;
							if (newa.getHeight()>maxh)
							{
								remaining=remaining+(newa.getHeight()-maxh);
								newa.setHeight(maxh);
							}
							else
							{
								busy++;
							}
						}
					}
					loop++;
				}
			}
			int pos=0;
			for (loop=0; loop<components.size(); loop++)
			{
				Component thisc = (Component)components.get(loop);
				Rectangle newa = (Rectangle)areas.get(thisc);
				newa.setOrigin(0,pos);
				pos=newa.getBottom()+1;
				if ((thisc.getMaximumWidth()==-1)||(getWidth()<=thisc.getMaximumWidth()))
				{
					newa.setWidth(getWidth());
				}
				else
				{
					newa.setWidth(thisc.getMaximumWidth());
				}
				thisc.setSize(newa.getWidth(),newa.getHeight());
			}
			assert pos==getHeight(): ""+pos+" should be "+getHeight();
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
		if (components.size()>0)
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
		else
		{
			return -1;
		}
	}
	
	public int getMaximumWidth()
	{
		if (components.size()>0)
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
		else
		{
			return -1;
		}
	}
}
