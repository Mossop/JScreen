package com.esp.jscreen.telnet;

import com.esp.jscreen.Connection;
import com.esp.jscreen.events.TerminalEvent;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;

public class TelnetConnection extends Connection
{
	private SocketChannel client;
	private TelnetControl telnethandler;
	private TerminalControl terminalhandler;
	
	TelnetConnection(SocketChannel client)
	{
		this.client=client;
		width=80;
		height=25;
		telnethandler = new TelnetControl(this);
		terminalhandler = new ProgrammableTerminal(this);
	}
	
	void setSize(int width, int height)
	{
		this.width=width;
		this.height=height;
		processEvent(new TerminalEvent(this,TerminalEvent.RESIZE));
	}
	
	void processData(ByteBuffer buffer)
	{
		buffer=telnethandler.processData(buffer);
		terminalhandler.processData(buffer);
	}
	
	public void send(ByteBuffer data)
	{
		try
		{
			client.write(data);
		}
		catch (Exception e)
		{
		}
	}
	
	public String toString()
	{
		return client.socket().getInetAddress().getHostAddress()+":"+client.socket().getPort();
	}
}
