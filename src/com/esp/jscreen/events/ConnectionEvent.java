package com.esp.jscreen.events;

/**
	* Indicates that something interesting has happened to the connection.
	*/
public class ConnectionEvent extends EventObject
{	
	/**
		* Indicates that the connection has been terminated
		*/
	public static final int CLOSE = 1;
	public static final int OPEN = 1;
	
	private int event;
	
	public ConnectionEvent(Object source, int code)
	{
		super(source);
		this.event=code;
	}
	
	public int getID()
	{
		return event;
	}
	
	public String toString()
	{
		return source+" triggered ConnectionEvent "+event;
	}
}
