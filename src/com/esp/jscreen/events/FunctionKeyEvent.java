package com.esp.jscreen.events;

/**
	* This event refers to function codes, i.e. F1, F2 etc.
	*/
public class FunctionKeyEvent extends KeyEvent
{
	public static final char F1 = '1';
	public static final char F2 = '2';
	public static final char F3 = '3';
	public static final char F4 = '4';
	public static final char F5 = '5';
	public static final char F6 = '6';
	public static final char F7 = '7';
	public static final char F8 = '8';
	public static final char F9 = '9';
	public static final char F10 = '\u0040';
	public static final char F11 = '\u0041';
	public static final char F12 = '\u0042';
	
	public FunctionKeyEvent(Object source, char key)
	{
		super(source,key);
		if ((key<F1)||(key>F12))
		{
			throw new IllegalArgumentException("Illegal function key");
		}
	}
	
	public String toString()
	{
		return source+" fired FunctionKeyEvent with key F"+key;
	}
}
