package com.esp.jscreen.events;

/**
	* Indicates that something interesting has happened to the terminal.
	*/
public class TerminalEvent extends EventObject
{
	/**
		* Indicates that the terminals size has changed. Anyone interested
		* should ask the connection about it.
		*/
	public static final int RESIZE = 1;
	
	private int event;
	
	public TerminalEvent(Object source, int code)
	{
		super(source);
		this.event=code;
	}
	
	public int getEvent()
	{
		return event;
	}
	
	public String toString()
	{
		return source+" triggered TerminalEvent "+event;
	}
}
