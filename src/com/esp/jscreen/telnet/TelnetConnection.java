package com.esp.jscreen.telnet;

import com.esp.jscreen.Connection;
import com.esp.jscreen.text.ColouredString;
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
	
	void send(ByteBuffer data)
	{
		try
		{
			client.write(data);
		}
		catch (Exception e)
		{
		}
	}
	
	public void beep()
	{
		ByteBuffer buffer = ByteBuffer.allocate(20);
		terminalhandler.beep(buffer);
		send(buffer);
	}
	
	public void clearScreen()
	{
		ByteBuffer buffer = ByteBuffer.allocate(20);
		terminalhandler.clearScreen(buffer);
		send(buffer);
	}
	
	public void writeText(int col, int row, ColouredString text)
	{
		ByteBuffer buffer = ByteBuffer.allocate(200);
		terminalhandler.moveCursor(col,row,buffer);
		terminalhandler.writeText(text,buffer);
		terminalhandler.moveCursor(cursorcol,cursorrow,buffer);
		send(buffer);
	}
	
	public void writeText(int col, int row, String text)
	{
		ByteBuffer buffer = ByteBuffer.allocate(200);
		terminalhandler.moveCursor(col,row,buffer);
		terminalhandler.writeText(text,buffer);
		terminalhandler.moveCursor(cursorcol,cursorrow,buffer);
		send(buffer);
	}
	
	protected void moveCursor(int col, int row)
	{
		ByteBuffer buffer = ByteBuffer.allocate(20);
		terminalhandler.moveCursor(col,row,buffer);
		send(buffer);
	}

	protected void writeText(ColouredString text)
	{
		ByteBuffer buffer = ByteBuffer.allocate(20);
		terminalhandler.writeText(text,buffer);
		send(buffer);
	}
	
	protected void writeText(String text)
	{
		ByteBuffer buffer = ByteBuffer.allocate(20);
		terminalhandler.writeText(text,buffer);
		send(buffer);
	}
	
	public String toString()
	{
		return client.socket().getInetAddress().getHostAddress()+":"+client.socket().getPort();
	}
}
