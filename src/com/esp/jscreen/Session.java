package com.esp.jscreen;

import com.esp.jscreen.events.EventObject;

public class Session
{
	private Connection connection;
	
	public Session(Connection conn)
	{
		connection=conn;
	}
	
	public void processEvent(EventObject event)
	{
	}
}
