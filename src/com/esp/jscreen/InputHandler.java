package com.esp.jscreen;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import java.util.Iterator;
import com.esp.jscreen.components.Test;

public class InputHandler implements Runnable
{
	private int inetport;
	
	public InputHandler(int port)
	{
		inetport=port;
		(new Thread(this)).start();
	}

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
					if (key.isAcceptable())
					{
						assert key.channel()==listener;
						SocketChannel client = listener.accept();
						client.configureBlocking(false);
						MultiAppSession sess = new MultiAppSession();
						new Test(sess);
						TelnetControl control = new TelnetControl(client,sess);
						control.detectTerminal();
						client.register(selector,SelectionKey.OP_READ,control);
					}
					else if (key.isReadable())
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
				}
			}
		}
		catch (Exception e)
		{
			System.err.println("Error setting up listener - "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		new InputHandler(2222);
	}
}
