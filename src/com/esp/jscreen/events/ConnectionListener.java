package com.esp.jscreen.events;

public interface ConnectionListener
{
	public boolean connectionClosed(ConnectionEvent e);
	
	public boolean connectionOpened(ConnectionEvent e);
}
