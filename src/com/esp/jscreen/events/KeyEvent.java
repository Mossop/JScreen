package com.esp.jscreen.events;

/**
	* We divert a little from the awt event model. There are a number
	* of different types of KeyEvents, including cursor, control and function.
	* This is the basic one, if its one of these then we have a simple ASCII
	* character.
	*/
public class KeyEvent extends ComponentEvent
{
	public static final char ENTER = '\n';
	public static final char SPACE = ' ';
	public static final char ESCAPE = '\u001B';
	public static final char NEWLINE = '\r';
	public static final char PAUSE = '\u0007';
	public static final char BACKSPACE = '\u0008';
	public static final char TAB = '\u0009';
	public static final char DELETE = '\u007F';

	protected char key;
	
	public KeyEvent(Object source, char key)
	{
		super(source,null);
		this.key=key;
	}
	
	public char getKey()
	{
		return key;
	}
	
	public boolean isAlphaNumeric()
	{
		return !Character.isISOControl(key);
	}
	
	public String toString()
	{
		return source+" fired KeyEvent with key "+key;
	}
}
