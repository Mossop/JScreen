package com.esp.jscreen.components;

import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.commands.Command;

public abstract class Component
{
	protected Container parent;
	protected int height;
	protected int width;
	
	public Component(Container parent)
	{
		this.parent=parent;
		if (parent!=null)
		{
			parent.addComponent(this);
		}
	}
	
	void setSize(int width, int height)
	{
		this.height=height;
		this.width=width;
		sizeChanged();
	}
	
	protected void sizeChanged()
	{
	}
	
	protected void gotFocus()
	{
		parent.moveCursor(this,0,0);
	}
	
	protected void lostFocus()
	{
	}
	
	public abstract ColouredString getLine(int line);
	
	public void newCommand(Command command)
	{
	}
	
	public void newText(StringBuffer text)
	{
	}
	
	public int getMinimumHeight()
	{
		return 1;
	}
	
	public int getMinimumWidth()
	{
		return 1;
	}
	
	public int getMaximumHeight()
	{
		return -1;
	}
	
	public int getMaximumWidth()
	{
		return -1;
	}

	boolean selectFirst()
	{
		gotFocus();
		return true;
	}
	
	boolean selectNext()
	{
		lostFocus();
		return false;
	}
	
	protected void lineUpdate(int line, ColouredString text)
	{
		lineUpdate(line,0,text);
	}
	
	protected void lineUpdate(int line, int start, ColouredString text)
	{
		parent.lineUpdate(this,line,start,text);
	}
}
