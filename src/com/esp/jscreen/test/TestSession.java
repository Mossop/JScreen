package com.esp.jscreen.test;

import com.esp.jscreen.Session;
import com.esp.jscreen.Connection;
import com.esp.jscreen.Window;
import com.esp.jscreen.widgets.StatusBar;
import com.esp.jscreen.widgets.VerticalContainer;
import com.esp.jscreen.events.EventObject;
import com.esp.jscreen.events.ControlKeyEvent;
import com.esp.jscreen.events.ConnectionEvent;

public class TestSession extends Session
{
	private StatusBar bottom;
	private Window win;
	
	public TestSession(Connection conn)
	{
		super(conn);
		win = new Window(this,"Main Window");
		bottom = new StatusBar();
		bottom.setText("Top");
		win.addComponent(bottom);
		win.addComponent(new VerticalContainer());
		bottom = new StatusBar();
		bottom.setText("Bottom");
		win.addComponent(bottom);
		win.show();
	}
	
	public void processEvent(EventObject event)
	{
		if (!(event instanceof ConnectionEvent))
		{
			bottom.setText(event.toString());
		}
		if (event instanceof ControlKeyEvent)
		{
			if (((ControlKeyEvent)event).getKey()=='A')
			{
				System.out.println (this);
			}
			else if (((ControlKeyEvent)event).getKey()=='Q')
			{
				win.hide();
			}
		}
		super.processEvent(event);
	}
}
