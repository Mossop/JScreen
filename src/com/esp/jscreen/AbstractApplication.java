package com.esp.jscreen;

import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.commands.Command;

public abstract class AbstractApplication implements Application
{
	protected MultiAppSession session;
	
	public AbstractApplication(MultiAppSession session)
	{
		this.session=session;
	}
	
	public abstract ColouredString getLine(int row);
	
	public void newCommand(Command command)
	{
	}

	public void newText(StringBuffer text)
	{
	}
	
	public void setSize(int width, int height)
	{
	}
	
	public void close()
	{
	}
}
