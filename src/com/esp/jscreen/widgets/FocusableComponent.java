package com.esp.jscreen.widgets;

import com.esp.jscreen.Component;
import com.esp.jscreen.Focusable;
import com.esp.jscreen.events.FocusListener;
import com.esp.jscreen.events.FocusEvent;
import java.util.List;
import java.util.ArrayList;

public abstract class FocusableComponent extends Component implements Focusable
{
	private List focuslisteners;
	private int cursorx;
	private int cursory;
	private boolean gotfocus;
	
	public FocusableComponent()
	{
		focuslisteners = new ArrayList();
		cursorx=0;
		cursory=0;
		gotfocus=false;
	}
	
	protected void setCursorPos(int x, int y)
	{
		cursorx=x;
		cursory=y;
		if (gotfocus)
		{
			getParent().moveCursor(this,x,y);
		}
	}
	
	protected boolean gotFocus()
	{
		return gotfocus;
	}
	
	protected Component focusNext()
	{
		if (gotfocus)
		{
			return null;
		}
		else
		{
			return this;
		}
	}
	
	public void addFocusListener(FocusListener listener)
	{
		focuslisteners.add(listener);
	}

	public void removeFocusListener(FocusListener listener)
	{
		focuslisteners.remove(listener);
	}
	
	public boolean focusLost(FocusEvent e)
	{
		gotfocus=false;
		boolean used = false;
		int loop=0;
		while ((loop<focuslisteners.size())&&(!used))
		{
			used=((FocusListener)focuslisteners.get(loop)).focusLost(e);
			loop++;
		}
		return used;
	}
	
	public boolean focusGained(FocusEvent e)
	{
		gotfocus=true;
		getParent().moveCursor(this,cursorx,cursory);
		boolean used = false;
		int loop=0;
		while ((loop<focuslisteners.size())&&(!used))
		{
			used=((FocusListener)focuslisteners.get(loop)).focusGained(e);
			loop++;
		}
		return used;
	}
}
