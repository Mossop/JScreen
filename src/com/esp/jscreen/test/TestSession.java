package com.esp.jscreen.test;

import com.esp.jscreen.Session;
import com.esp.jscreen.Connection;
import com.esp.jscreen.Window;
import com.esp.jscreen.widgets.Label;
import com.esp.jscreen.widgets.TextField;
import com.esp.jscreen.widgets.VerticalContainer;
import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.events.ControlKeyEvent;
import com.esp.jscreen.events.ConnectionEvent;

public class TestSession extends Session
{
	private Window win;
	
	public TestSession(Connection conn)
	{
		super(conn);
		win = new Window(this,"Main Window");
		win.addComponent(new Label("Test"));
		win.addComponent(new TextField());
		win.addComponent(new VerticalContainer());
		win.show();
	}
}
