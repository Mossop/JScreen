package com.esp.jscreen.commands;

public class Command
{
	private String description;
	
	public Command(String desc)
	{
		description=desc;
	}
	
	public String toString()
	{
		return description;
	}
}
