package com.esp.jscreen.telnet;

import com.esp.jscreen.ConnectionHandler;

/**
  * The inputhandler handles the routing of all connections and new
  * data from the network. Using the New I/O capabilities, this
  * is all handled in a single thread. Once a telnet connection is
  * established, it is assiged a {@link com.esp.jscreen.TelnetControl TelnetControl}
  * to deal with all the actual data.
  *
  * @author Dave Townsend
  */
public class TelnetConnectionHandler extends ConnectionHandler implements Runnable
{
	/**
	  * Simply holds the port we should be listening on.
	  */
	private int inetport;
	
	/**
	  * Creates the inputhandles on a particular port.
	  *
	  * @param port The tcp/ip port to listen on.
	  */
	public InputHandler(int port)
	{
		inetport=port;
		(new Thread(this)).start();
	}

	/**
	  * Called when a new connection has been extablished.
	  * This accepts the connection, then adds the new connection to the
	  * selector. Also creates a new {@link com.esp.jscreen.TelnetControl TelnetControl}
	  * for the data. This is attached to the client.
	  *
	  * @param listener The ServerSocketChannel that heard the connection.
	  * @param selector The Selector to add the client to.
	  * @throws Any Exception that occurs in the I/O calls.
	  */
	private void newConnection(ServerSocketChannel listener, Selector selector) throws Exception
	{
		SocketChannel client = listener.accept();
		client.configureBlocking(false);
		MultiAppSession sess = new MultiAppSession();
		new Test(sess);
		TelnetControl control = new TelnetControl(client,sess);
		control.detectTerminal();
		client.register(selector,SelectionKey.OP_READ,control);
	}
	
	/**
		* Called when some new data arrives.
		* This is also called when a connection is closed, so first we check if
		* the data count is -1. If it is then we tell everything that the
		* connection is closed. Otherwise we pass the data onto the clients
		* attachment.
		*
		* @param key The SelectionKey that is ready for reading.
		* @param buffer A buffer to use for reading the data.
		*/
	private void newData(SelectionKey key, ByteBuffer buffer)
	{
		int count;
		try
		{
			count=((SocketChannel)key.channel()).read(buffer);
		}
		catch (Exception e)
		{
			count=-1;
		}
		if (count>=0)
		{
			buffer.flip();
			((TelnetControl)key.attachment()).newData(buffer);
			buffer.clear();
		}
		else
		{
			((TelnetControl)key.attachment()).close();
		}
	}
	
	/**
		* This thread handles the setup of the ServerSocketChannel and the
		* initial handling of all data.
		*/
	public void run()
	{
		try
		{
			ByteBuffer buffer = ByteBuffer.allocate(128);
			ServerSocketChannel listener = ServerSocketChannel.open();
			listener.configureBlocking(false);
			listener.socket().bind(new InetSocketAddress(inetport));
			Selector selector = Selector.open();
			listener.register(selector,SelectionKey.OP_ACCEPT);
			while (true)
			{
				selector.select();
				Iterator loop = selector.selectedKeys().iterator();
				while (loop.hasNext())
				{
					SelectionKey key = (SelectionKey)loop.next();
					loop.remove();
					try
					{
						if (key.isAcceptable())
						{
							assert key.channel()==listener;
							newConnection(listener, selector);
						}
						else if (key.isReadable())
						{
							newData(key,buffer);
						}
					}
					catch (Throwable e)
					{
						System.out.println("Exception handling io: "+e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
		catch (Exception e)
		{
			System.err.println("Error setting up listener - "+e.getMessage());
			e.printStackTrace();
		}
	}
}
