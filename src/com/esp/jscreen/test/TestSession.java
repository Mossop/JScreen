package com.esp.jscreen.test;

import com.esp.jscreen.Session;
import com.esp.jscreen.Connection;
import com.esp.jscreen.events.EventObject;

public class TestSession extends Session
{
	public TestSession(Connection conn)
	{
		super(conn);
	}
	
	public void processEvent(EventObject event)
	{
		System.out.println(event);
	}
}
