package com.esp.jscreen.events;

/**
	* This event refers to control codes, i.e. Ctrl-A etc.
	*/
public class ControlKeyEvent extends KeyEvent
{
	public ControlKeyEvent(Object source, char key)
	{
		super(source,key);
	}
	
	public String toString()
	{
		return source+" fired ControlKeyEvent with key Ctrl-"+key;
	}
}
