package com.esp.jscreen.events;

import com.esp.jscreen.Component;

public class ComponentEvent extends EventObject
{
	private Component component;
	
	public ComponentEvent(Object source, Component comp)
	{
		super(source);
		component=comp;
	}
	
	public Component getComponent()
	{
		return component;
	}

	public String toString()
	{
		return source+" fired ComponentEvent on component "+source.getClass().getName();
	}
}
