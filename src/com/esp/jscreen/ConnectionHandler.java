package com.esp.jscreen;

/**
	* The ConnectionHandler is an abstract way of accepting connections.
	* It will allow us in the future to allow people to connect from more
	* than just telnet. Serial ports, standard input are other possibilities.
	*
  * @author Dave Townsend
	*/
public abstract class ConnectionHandler
{
	private boolean blocked;
	private boolean started;
	protected Application app;
	
	public ConnectionHandler()
	{
		blocked=false;
		app=null;
	}
	
	/**
		* This physically starts the connection handler listening for connections.
		*
		* @param newapp The Application to use to create sessions.
		*/
	public void start(Application newapp)
	{
		if (!started)
		{
			app=newapp;
			started=true;
		}
		else
		{
			throw new IllegalStateException("Cannot start a connection that has already been started");
		}
	}
	
	/**
		* The connection handler can be stopped by calling this method.
		*/
	public void stop()
	{
		started=false;
	}
	
	/**
		* This method safely checks whether incoming connections are blocked for
		* the moment.
		*/
	public synchronized boolean isBlocked()
	{
		return blocked;
	}
	
	/**
		* Stops the connection handler from accepting connections for the time being.
		* It is not legal to block the connection before it has been started.
		*/
	public synchronized void block()
	{
		if (!started)
		{
			blocked=true;
		}
		else
		{
			throw new IllegalStateException("Cannot block a connection that has not been started");
		}
	}

	/**
		* Starts the connection handler accepting connections again.
		*/
	public synchronized void unblock()
	{
		blocked=false;
	}
}
