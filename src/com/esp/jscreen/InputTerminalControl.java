package com.esp.jscreen;

import java.nio.ByteBuffer;

/**
	* This is the input side of the Terminal Control.
	* When data or information is received from the client, something
	* implementing this interface can be called to handle it.
	*
	* @author Dave Townsend
	*/
public interface InputTerminalControl
{
	/**
		* Returns the current session.
		*/
	public Session getSession();
	
	/**
		* Sets a new session for the client to use.
		*/
	public void setSession(Session newsession);
	
	/**
		* Sets the new window size.
		*
		* @param width The new width.
		* @param height The new height.
		*/
	public void setSize(int width, int height);

	/**
		* Passes some data to the terminal control.
		*
		* @param buffer A buffer containing the data.
		*/
	public void newData(ByteBuffer buffer);
	
	/**
		* Indicates that the client has disconnected.
		*/
	public void close();
}
