package com.esp.jscreen.commands;

public class CursorCommand extends Command
{
	public static final int CC_UNKNOWN = -1;
	
	public static final int CC_CURSORUP = 1;
	public static final int CC_CURSORDN = 2;
	public static final int CC_CURSORLT = 3;
	public static final int CC_CURSORRT = 4;
	public static final int CC_HOME = 5;
	public static final int CC_END = 6;
	public static final int CC_PGUP = 7;
	public static final int CC_PGDN = 8;
	public static final int CC_INSERT = 9;
	public static final int CC_FORMFEED = 10;
	
	private int command;
	
	public CursorCommand(int key)
	{
		super("CursorCommand "+key);
		command=key;
	}
	
	public int getKey()
	{
		return command;
	}
}
