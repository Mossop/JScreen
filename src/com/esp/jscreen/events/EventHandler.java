package com.esp.jscreen.events;

public class EventHandler
{
	public static boolean channelEvent(Object obj, EventObject event)
	{
		boolean result = false;
		if ((event instanceof ConnectionEvent)&&(obj instanceof ConnectionListener))
		{
			if (((ConnectionEvent)event).getID()==ConnectionEvent.OPEN)
			{
				result=result||((ConnectionListener)obj).connectionOpened((ConnectionEvent)event);
			}
			else if (((ConnectionEvent)event).getID()==ConnectionEvent.CLOSE)
			{
				result=result||((ConnectionListener)obj).connectionClosed((ConnectionEvent)event);
			}
		}
		else if ((event instanceof TerminalEvent)&&(obj instanceof TerminalListener))
		{
			result=result||((TerminalListener)obj).terminalResized((TerminalEvent)event);
		}
		else if (event instanceof ComponentEvent)
		{
			if ((event instanceof KeyEvent)&&(obj instanceof KeyListener))
			{
				result=result||((KeyListener)obj).keyPressed((KeyEvent)event);
			}
			else if ((event instanceof FocusEvent)&&(obj instanceof FocusListener))
			{
				if (((FocusEvent)event).getID()==FocusEvent.FOCUS_GAINED)
				{
					result=result||((FocusListener)obj).focusGained((FocusEvent)event);
				}
				else if (((FocusEvent)event).getID()==FocusEvent.FOCUS_LOST)
				{
					result=result||((FocusListener)obj).focusLost((FocusEvent)event);
				}
			}
		}
		return result;
	}
}
