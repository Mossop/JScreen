package com.esp.jscreen;

import com.esp.jscreen.commands.Command;

abstract class AbstractSession implements Session
{
	protected OutputTerminalControl terminal;
	
	public AbstractSession()
	{
		terminal=null;
	}
	
	public void setTerminal(OutputTerminalControl terminal)
	{
		this.terminal=terminal;
	}
	
	public void setSize(int width, int height)
	{
	}
	
	public void close()
	{
	}
	
	public abstract void newCommand(Command command);
	
	public abstract void newText(StringBuffer text);
}
