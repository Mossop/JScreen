package com.esp.jscreen;

public abstract class Application
{
	public Application()
	{
		startup();
	}
	
	public abstract Session createSession(Connection connection);
	
	private void startup()
	{
	}
}
