package com.esp.jscreen.components;

import com.esp.jscreen.MultiAppSession;

public class Test extends AppContainer
{
	public Test(MultiAppSession session)
	{
		super(session);
	}
	
	public void initialise()
	{
		new TextBox(this,"Test: ");
	}
}
