package com.esp.jscreen.events;

/**
	* This event refers to cursor codes, i.e. up, down etc.
	*/
public class CursorKeyEvent extends KeyEvent
{
	public static final char CURSORUP = '8';
	public static final char CURSORDN = '2';
	public static final char CURSORLT = '4';
	public static final char CURSORRT = '6';
	public static final char HOME = '7';
	public static final char END = '1';
	public static final char PGUP = '9';
	public static final char PGDN = '3';
	public static final char INSERT = '0';
	public static final char FORMFEED = 'F';

	public CursorKeyEvent(Object source, char key)
	{
		super(source,key);
	}
	
	public boolean isAlphaNumeric()
	{
		return false;
	}
	
	public String toString()
	{
		return source+" fired CursorKeyEvent with key "+key;
	}
}
