package com.esp.jscreen;

import com.esp.jscreen.AbstractSession;
import com.esp.jscreen.commands.*;
import com.esp.jscreen.text.ColouredString;
import java.util.ArrayList;
import java.util.List;

public class MultiAppSession extends AbstractSession
{
	private List apps;
	private Application currentapp;
	private boolean modal;
	private int cursorrow;
	private int cursorcol;
	
	public MultiAppSession()
	{
		super();
		cursorrow=0;
		cursorcol=0;
		apps = new ArrayList();
		currentapp=null;
		modal=false;
	}
	
	public void setTerminal(OutputTerminalControl terminal)
	{
		super.setTerminal(terminal);
		setSize(terminal.getWidth(), terminal.getHeight());
		refresh();
	}
	
	public void beep()
	{
		terminal.beep();
	}
	
	private void refresh()
	{
		if ((terminal!=null)&&(currentapp!=null))
		{
			terminal.setCursorVisibility(false);
			for (int loop=0; loop<terminal.getHeight(); loop++)
			{
				terminal.moveCursor(loop,0);
				terminal.writeLine(currentapp.getLine(loop));
			}
			terminal.moveCursor(cursorrow,cursorcol);
			terminal.setCursorVisibility(true);
		}
	}
	
	private void switchApp(Application newapp)
	{
		currentapp=newapp;
		refresh();
	}
	
	public void moveCursor(Application app, int row, int column)
	{
		if (app==currentapp)
		{
			cursorrow=row;
			cursorcol=column;
			if (terminal!=null)
			{
				terminal.moveCursor(cursorrow,cursorcol);
			}
		}
	}
	
	public void lineUpdate(Application app, int line)
	{
		if ((app==currentapp)&&(terminal!=null))
		{
			terminal.setCursorVisibility(false);
			terminal.moveCursor(line,0);
			terminal.writeLine(currentapp.getLine(line));
			terminal.moveCursor(cursorrow,cursorcol);
			terminal.setCursorVisibility(true);
		}
	}
	
	public void lineUpdate(Application app, int line, ColouredString text)
	{
		if ((app==currentapp)&&(terminal!=null))
		{
			terminal.setCursorVisibility(false);
			terminal.moveCursor(line,0);
			terminal.writeLine(text);
			terminal.moveCursor(cursorrow,cursorcol);
			terminal.setCursorVisibility(true);
		}
	}
	
	public void lineUpdate(Application app, int line, int start, ColouredString text)
	{
		if ((app==currentapp)&&(terminal!=null))
		{
			terminal.setCursorVisibility(false);
			terminal.moveCursor(line,start);
			terminal.writeText(text);
			terminal.moveCursor(cursorrow,cursorcol);
			terminal.setCursorVisibility(true);
		}
	}
	
	public void remove(Application oldapp)
	{
		int pos=apps.indexOf(oldapp)-1;
		if (pos<0)
		{
			pos=0;
		}
		apps.remove(oldapp);
		if (oldapp==currentapp)
		{
			modal=false;
			switchApp((Application)apps.get(0));
		}
	}
	
	public void show(Application newapp)
	{
		int pos=apps.indexOf(currentapp)+1;
		apps.add(newapp);
		switchApp(newapp);
	}
	
	public void showModal(Application newapp)
	{
		modal=true;
		show(newapp);
	}
	
	public void newCommand(Command command)
	{
		if ((command instanceof ControlCommand)&&(((ControlCommand)command).getKey()=='R'))
		{
			refresh();
		}
		else
		{
			if (currentapp!=null)
			{
				currentapp.newCommand(command);
			}
		}
	}

	public void newText(StringBuffer text)
	{
		if (currentapp!=null)
		{
			currentapp.newText(text);
		}
	}	
	
	public void setSize(int width, int height)
	{
		for (int loop=0; loop<apps.size(); loop++)
		{
			((Application)apps.get(loop)).setSize(width,height);
		}
		refresh();
	}
	
	public int getHeight()
	{
		if (terminal!=null)
		{
			return terminal.getHeight();
		}
		else
		{
			return 25;
		}
	}
	
	public int getWidth()
	{
		if (terminal!=null)
		{
			return terminal.getWidth();
		}
		else
		{
			return 80;
		}
	}
	
	public void close()
	{
		List oldapps = new ArrayList(apps);
		for (int loop=0; loop<oldapps.size(); loop++)
		{
			((Application)oldapps.get(loop)).close();
		}
	}
}
