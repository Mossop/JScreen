package com.esp.jscreen;

import com.esp.jscreen.commands.Command;

public interface Session
{
	public void setTerminal(OutputTerminalControl terminal);
	
	public void setSize(int width, int height);
	
	public void close();
	
	public void newCommand(Command command);
	
	public void newText(StringBuffer text);
}
