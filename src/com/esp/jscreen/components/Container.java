package com.esp.jscreen.components;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.commands.Command;

public abstract class Container extends Component
{
	protected List components;
	protected int selected;
	protected Map areas;
	protected boolean border;
	
	public Container(Container parent)
	{
		super(parent);
		selected=-1;
		components = new ArrayList();
		areas = new HashMap();
		border=true;
	}
	
	protected abstract void doLayout();
	
	public abstract int getMinimumHeight();
	
	public abstract int getMinimumWidth();
	
	public abstract int getMaximumHeight();
	
	public abstract int getMaximumWidth();

	public void newCommand(Command command)
	{
		if (selected>=0)
		{
			((Component)components.get(selected)).newCommand(command);
		}
	}
	
	public void beep()
	{
		parent.beep();
	}
	
	public void newText(StringBuffer text)
	{
		if (selected>=0)
		{
			((Component)components.get(selected)).newText(text);
		}
	}
	
	public void setBorder(boolean value)
	{
		if (border!=value)
		{
			border=value;
			doLayout();
		}
	}
	
	void moveCursor(Component comp, int row, int column)
	{
		int pos = components.indexOf(comp);
		if (pos==selected)
		{
			Area area = (Area)areas.get(comp);
			parent.moveCursor(this,row+area.getTop(), column+area.getLeft());
		}
	}
	
	void lineUpdate(Component comp, int line, ColouredString text)
	{
		lineUpdate(comp,line,0,text);
	}
	
	void lineUpdate(Component comp, int line, int start, ColouredString text)
	{
		Area area = (Area)areas.get(comp);
		line=line+area.getTop();
		start=start+area.getLeft();
		parent.lineUpdate(this,line,start,text);
	}
	
	void addComponent(Component newc)
	{
		components.add(newc);
	}
	
	void removeComponent(Component oldc)
	{
		if (components.indexOf(oldc)<selected)
		{
			selected--;
		}
		else if (components.indexOf(oldc)==selected)
		{
			if (!selectNext())
			{
				selectFirst();
			}
		}
		components.remove(oldc);
		doLayout();
	}
	
	boolean selectFirst()
	{
		if (components.size()>0)
		{
			selected=0;
			while ((selected<components.size())&&(!((Component)components.get(selected)).selectFirst()))
			{
				selected++;
			}
			if (selected==components.size())
			{
				selected=-1;
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			selected=-1;
			return false;
		}
	}
	
	boolean selectNext()
	{
		if (((Component)components.get(selected)).selectNext())
		{
			return true;
		}
		else
		{
			selected++;
			while ((selected<components.size())&&(!((Component)components.get(selected)).selectFirst()))
			{
				selected++;
			}
			if (selected==components.size())
			{
				selected=-1;
				return false;
			}
			else
			{
				return true;
			}
		}
	}
}
