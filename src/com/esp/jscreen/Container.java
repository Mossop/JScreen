package com.esp.jscreen;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.events.ComponentEvent;
import com.esp.jscreen.events.EventHandler;
import com.esp.jscreen.text.MultiLineBuffer;
import com.esp.jscreen.text.ColourInfo;

public abstract class Container extends Component
{
	protected List components;
	private boolean validated;
	protected Map areas;
	protected Component focussed;
	
	public Container()
	{
		components = new ArrayList();
		areas = new HashMap();
		validated=false;
		focussed=null;
	}
	
	protected abstract void doLayout();
	
	protected void layout()
	{
		doLayout();
		validated=true;
	}
	
	protected boolean isValidated()
	{
		return validated;
	}
	
	protected Component focusNext()
	{
		int pos=0;
		if (focussed!=null)
		{
			Component next = focussed.focusNext();
			if (next!=null)
			{
				return next;
			}
			pos=components.indexOf(focussed)+1;
		}
		while (pos<components.size())
		{
			Component next = ((Component)components.get(pos)).focusNext();
			if (next!=null)
			{
				focussed=(Component)components.get(pos);
				return next;
			}
			pos++;
		}
		focussed=null;
		return null;
	}
	
	public void setSize(int width, int height)
	{
		super.setSize(width,height);
		if (validated)
		{
			doLayout();
		}
	}
	
	protected void setHeight(int height)
	{
		super.setHeight(height);
		if (validated)
		{
			doLayout();
		}
	}
	
	protected void setWidth(int width)
	{
		super.setWidth(width);
		if (validated)
		{
			doLayout();
		}
	}
	
	public void addComponent(Component newc)
	{
		components.add(newc);
		areas.put(newc,new Rectangle());
		newc.setParent(this);
		if (validated)
		{
			doLayout();
		}
	}
	
	public void removeComponent(Component newc)
	{
		if (newc==focussed)
		{
			getFrame().focusNext();
		}
		components.remove(newc);
		areas.remove(newc);
		newc.setParent(null);
		if (validated)
		{
			doLayout();
		}
	}
	
	protected MultiLineBuffer getDisplay(Rectangle area)
	{
		//System.out.println (getClass().getName()+" "+area);
		MultiLineBuffer display = new MultiLineBuffer(getBackgroundColour(),area);
		for (int loop=0; loop<components.size(); loop++)
		{
			Component thisc = (Component)components.get(loop);
			Rectangle carea = (Rectangle)areas.get(thisc);
			if (carea==null)
			{
				assert false;
				carea = new Rectangle();
			}
			Rectangle drawarea = area.union(carea);
			if (drawarea.getArea()>0)
			{
				int x=drawarea.getLeft()-area.getLeft();
				int y=drawarea.getTop()-area.getTop();
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
	
	protected boolean processEvent(EventObject event)
	{
		if (!EventHandler.channelEvent(this,event))
		{
			if (event instanceof ComponentEvent)
			{
				if (focussed!=null)
				{
					return focussed.processEvent(event);
				}
				else
				{
					return false;
				}
			}
			else
			{
				for (int loop=0; loop<components.size(); loop++)
				{
					if (((Component)components.get(loop)).processEvent(event))
					{
						return true;
					}
				}
				return false;
			}
		}
		else
		{
			return true;
		}
	}
	
	void updateComponent(Component comp, Rectangle area)
	{
		if (getParent()!=null)
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
	
	public void moveCursor(Component comp, int x, int y)
	{
		if (getParent()!=null)
		{
			if (comp==focussed)
			{
				Rectangle comparea = (Rectangle)areas.get(comp);
				x=x+comparea.getLeft();
				y=y+comparea.getTop();
				getParent().moveCursor(this,x,y);
			}
			else
			{
				throw new IllegalArgumentException("Component that is not focussed is trying to move the cursor");
			}
		}
	}
	
	protected String toString(String indent)
	{
		StringBuffer result = new StringBuffer();
		result.append(indent+"Container: "+getClass().getName()+"\n");
		for (int loop=0; loop<components.size(); loop++)
		{
			result.append(((Component)components.get(loop)).toString(indent+"  "));
			result.append(indent+"    ");
			Object area = areas.get(components.get(loop));
			if (area==null)
			{
				area = new Rectangle();
			}
			result.append(area+"\n");
		}
		return result.toString();
	}
}
