package com.esp.jscreen.commands;

public class TextCommand extends Command
{
	public static final int TC_UNKNOWN = -1;
	
	public static final int TC_NEWLINE = 1;
	public static final int TC_PAUSE = 2;
	public static final int TC_BACKSPACE = 3;
	public static final int TC_DELETE = 4;
	public static final int TC_TAB = 5;
	public static final int TC_ESCAPE = 5;

	private int command;
	
	public TextCommand(int key)
	{
		super("TextCommand "+key);
		command=key;
	}
	
	public int getKey()
	{
		return command;
	}
}
