package com.esp.jscreen;

/**
 * The Application is what the client wants to connect to.
 * In general each process will have one Application running
 * with multiple sessions - one per connected client.
 * It is expected that the Application will contain the static main method to
 * start the system and will start any necessary connection handlers
 * in the startup method.
 */

public abstract class Application
{
	/**
	 * Some basic initialisation for the Application. This should not be overridden.
	 * After the initialisation the startup method is called which should be overriden
	 * for any necessary Application specific setup.
	 */
	public Application()
	{
		startup();
	}

	/**
	 * A ConnectionHandler can call this method to retrieve a Session for a new Connection
	 */
	public abstract Session createSession(Connection connection);

	/**
	 * This empty method is called after all initialisation. It may be overridden freely.
	 */
	private void startup()
	{
	}
}
