package com.esp.jscreen;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.esp.jscreen.text.MultiLineBuffer;

public class Window extends Frame
{
	private Session session;
	private List subframes;
	private Frame currentframe;
	
	public Window(Session session, String name)
	{
		super(null,name);
		this.session=session;
		subframes = new ArrayList();
		currentframe=this;
		border=false;
	}
	
	void addFrame(Frame newframe)
	{
		subframes.add(newframe);
		changeFrame(newframe);
	}
	
	private void changeFrame(Frame newframe)
	{
		currentframe=newframe;
	}
	
	void removeFrame(Frame oldframe)
	{
		int index = subframes.indexOf(oldframe);
		if (index>=0)
		{
			if (oldframe==currentframe)
			{
				index--;
				if (index<0)
				{
					changeFrame(this);
				}
				else
				{
					changeFrame((Frame)subframes.get(index-1));
				}
			}
			subframes.remove(oldframe);
		}
		else
		{
			throw new IllegalArgumentException("No such frame in this window");
		}
	}
	
	public void show()
	{
		if (visible)
		{
			session.changeWindow(this);
		}
		else
		{
			session.addWindow(this);
			visible=true;
		}
	}
	
	public void hide()
	{
		session.removeWindow(this);
		visible=false;
	}
	
	void updateFrame(Frame frame, Rectangle area)
	{
		int z = subframes.indexOf(frame);
		if ((z>=0)||(frame==this))
		{
			area=area.union(session.getViewPort()).union(frame.getArea());
			if (area.getArea()>0)
			{
				List areas = new ArrayList();
				List nextareas;
				Rectangle framearea;
				areas.add(area);
				for (int loop=subframes.size()-1; ((loop>z)&&(areas.size()>0)); loop--)
				{
					nextareas = new ArrayList();
					framearea=((Frame)subframes.get(loop)).getArea();
					for (int subloop=0; subloop<areas.size(); subloop++)
					{
						Rectangle thisrect = (Rectangle)areas.get(subloop);
						nextareas.addAll(Arrays.asList(thisrect.subtract(framearea)));
					}
					areas=nextareas;
				}
				for (int loop=0; loop<areas.size(); loop++)
				{
					Rectangle thisrect = (Rectangle)areas.get(loop);
					MultiLineBuffer lines = frame.getDisplay(thisrect);
					session.updateDisplay(this,thisrect,lines);
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("No such frame in this window");
		}
	}
	
	public void move(int x, int y)
	{
		throw new IllegalArgumentException("You cant move the window");
	}
	
	public void setSize(int width, int height)
	{
		throw new IllegalArgumentException("You cant change the size of the window");
	}
	
	public void setHeight(int height)
	{
		throw new IllegalArgumentException("You cant change the size of the window");
	}
	
	public void setWidth(int width)
	{
		throw new IllegalArgumentException("You cant change the size of the window");
	}
	
	public int getWidth()
	{
		return session.getWidth();
	}
	
	public int getHeight()
	{
		return session.getHeight();
	}
	
	protected Window getWindow()
	{
		return this;
	}
	
	protected Session getSession()
	{
		return session;
	}
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append("  Window: "+getName()+"\n");
		result.append(super.toString());
		for (int loop=0; loop<subframes.size(); loop++)
		{
			result.append(subframes.get(loop).toString());
		}
		return result.toString();
	}
}
