package com.esp.jscreen;

import java.nio.ByteBuffer;

public interface InputTerminalControl
{
	public Session getSession();
	
	public void setSession(Session newsession);
	
	public void setSize(int width, int height);

	public void newData(ByteBuffer buffer);
	
	public void close();
}
