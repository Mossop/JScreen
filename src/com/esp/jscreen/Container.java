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

/**
	* The Container is a component that can hold other components.
	* This abstract class provides some methods for managing a group of
	* components. A container only needs override the doLayout method to
	* place components and this class can then handle everything else in
	* a basic fashion.
	*/
public abstract class Container extends Component
{
	/**
		* This list holds the components that this container contains.
		*/
	protected List components;
	/**
		* The container is validated if it has been laid out since the last
		* component was added.
		*/
	private boolean validated;
	/**
		* This maps the component to an area defined by a Rectangle.
		*/
	protected Map areas;
	/**
		* The container must remember which component is focussed at the moment.
		*/
	protected Component focussed;

	/**
		* Some basic initialisation.
		*/
	public Container()
	{
		components = new ArrayList();
		areas = new HashMap();
		validated=false;
		focussed=null;
	}

	/**
		* An implementation of the container must override this method.
		* The method should take the components from the list and define a Rectangle
		* for each one defining its position within the container. These must
		* be placed into the areas map.
		*/
	protected abstract void doLayout();

	/**
		* Forces the container to validate itself.
		*/
	protected void layout()
	{
		doLayout();
		validated=true;
	}

	/**
		* Tests whether the component has been validated.
		*/
	protected boolean isValidated()
	{
		return validated;
	}

	/**
		* This tries to find the next component that can be focussed.
		*/
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

	/**
		* Called to change the size of the container.
		* If the container is validated, it gets laid out.
		*/
	public void setSize(int width, int height)
	{
		super.setSize(width,height);
		if (validated)
		{
			doLayout();
		}
	}

	/**
		* Called to change the size of the container.
		* If the container is validated, it gets laid out.
		*/
	protected void setHeight(int height)
	{
		super.setHeight(height);
		if (validated)
		{
			doLayout();
		}
	}

	/**
		* Called to change the size of the container.
		* If the container is validated, it gets laid out.
		*/
	protected void setWidth(int width)
	{
		super.setWidth(width);
		if (validated)
		{
			doLayout();
		}
	}

	/**
		* Adds a component to the container.
		*/
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

	/**
		* Removes a component from the container.
		*/
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

	/**
		* This retrieves an area of the container for display.
		*/
	protected MultiLineBuffer getDisplay(Rectangle area)
	{
		//System.out.println (getClass().getName()+" "+area);
		MultiLineBuffer display = new MultiLineBuffer(getPalette().getColour("FRAME"),area);
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

	/**
		* Uses the eventhandler to direct events.
		*/
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

	/**
		* Components call this to signal that some area of them has updated.
		*/
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

	/**
		* Called by components to move the cursor to somewhere new.
		*/
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

	/**
		* Retrieves a debug string for the container.
		*/
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
