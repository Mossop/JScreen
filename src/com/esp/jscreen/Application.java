package com.esp.jscreen;

import com.esp.jscreen.commands.Command;
import com.esp.jscreen.text.ColouredString;

public interface Application
{
	public ColouredString getLine(int row);
	
	public void newCommand(Command command);

	public void newText(StringBuffer text);	
	
	public void setSize(int width, int height);
	
	public void close();
}
