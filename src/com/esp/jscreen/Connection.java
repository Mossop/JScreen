package com.esp.jscreen;

import java.nio.ByteBuffer;
import com.esp.jscreen.events.EventObject;

public abstract class Connection
{
	/**
		* The current screen width.
		*/
  protected int width;
	/**
		* The current screen height.
		*/
  protected int height;
	/**
		* The session this object belongs to.
		*/
	protected Session session = null;
	
	/**
		* Returns the width of the client.
		*
		* @return The width.
		*/
	public int getWidth()
	{
		return width;
	}
	
	/**
		* Returns the height of the client.
		*
		* @return The height.
		*/
	public int getHeight()
	{
		return height;
	}

	public void setSession(Session newsess)
	{
		session=newsess;
	}
	
	public abstract void send(ByteBuffer data);
	
	public void processEvent(EventObject event)
	{
		if (session!=null)
		{
			session.processEvent(event);
		}
	}
}
