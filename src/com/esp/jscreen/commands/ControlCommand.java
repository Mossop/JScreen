package com.esp.jscreen.commands;

public class ControlCommand extends Command
{
	private char key;
	
	public ControlCommand(char key)
	{
		super("Control-"+Character.toUpperCase(key));
		this.key=key;
	}
	
	public char getKey()
	{
		return key;
	}
}
