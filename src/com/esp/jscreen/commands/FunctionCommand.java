package com.esp.jscreen.commands;

public class FunctionCommand extends Command
{
	private int function;
	
	public FunctionCommand(int function)
	{
		super("F-"+function);
		this.function=function;
	}
	
	public int getFunction()
	{
		return function;
	}
}
