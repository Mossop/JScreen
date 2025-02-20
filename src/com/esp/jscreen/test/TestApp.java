package com.esp.jscreen.test;

import com.esp.jscreen.Session;
import com.esp.jscreen.Frame;
import com.esp.jscreen.Window;
import com.esp.jscreen.Application;
import com.esp.jscreen.Connection;
import com.esp.jscreen.telnet.TelnetConnectionHandler;

public class TestApp extends Application
{
	public TestApp()
	{
		super();
		(new TelnetConnectionHandler(2222)).start(this);
	}

	public Session createSession(Connection connection)
	{
		System.out.println("Creating session for "+connection);
		TestSession test = new TestSession(connection);
		return test;
	}

	public static void main(String[] args)
	{
		new TestApp();
	}
}
