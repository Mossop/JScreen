package com.esp.jscreen.events;

import com.esp.jscreen.Component;

public class FocusEvent extends ComponentEvent
{
	public static final int FOCUS_LOST = 1;
	public static final int FOCUS_GAINED = 2;
	
	private Component other;
	private int id;
	
	public FocusEvent(Object source, Component comp, int id, Component other)
	{
		super(source,comp);
		this.id=id;
	}
	
	public int getID()
	{
		return id;
	}
	
	public Component getOppositeComponent()
	{
		return other;
	}

	public String toString()
	{
		return source+" fired FocusEvent on component "+source.getClass().getName();
	}
}
