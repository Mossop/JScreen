package com.esp.jscreen.components;

import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.Application;
import com.esp.jscreen.commands.Command;
import com.esp.jscreen.commands.TextCommand;
import com.esp.jscreen.MultiAppSession;

public abstract class AppContainer extends VerticalContainer implements Application
{
	protected MultiAppSession session;
	
	public AppContainer(MultiAppSession session)
	{
		super(null);
		height=session.getHeight();
		width=session.getWidth();
		this.session=session;
		initialise();
		doLayout();
		session.show(this);
		selectFirst();
	}
	
	public void initialise()
	{
	}
	
	public void beep()
	{
		session.beep();
	}
	
	void moveCursor(Component comp, int row, int column)
	{
		Area area = (Area)areas.get(comp);
		session.moveCursor(this,row+area.getTop(), column+area.getLeft());
	}
	
	void lineUpdate(Component comp, int line, int start, ColouredString text)
	{
		Area area = (Area)areas.get(comp);
		line=line+area.getTop();
		start=start+area.getLeft();
		session.lineUpdate(this,line,start,text);
	}

	public void setSize(int width, int height)
	{
		super.setSize(width,height);
		doLayout();
	}
	
	public void newCommand(Command command)
	{
		if ((command instanceof TextCommand)&&(((TextCommand)command).getKey()==TextCommand.TC_TAB))
		{
			if (!selectNext())
			{
				selectFirst();
			}
		}
		else
		{
			super.newCommand(command);
		}
	}

	public void close()
	{
	}
}
