package com.esp.jscreen;

import java.nio.ByteBuffer;
import com.esp.jscreen.text.ColouredString;

/**
	* The Session can pass information back to the client through this
	* interface to the terminal control.
	*
	* @author Dave Townsend
	*/
public interface OutputTerminalControl
{
	/**
		* Allows the session to retrieve the height of the clients window.
		*
		* @return The height of the clients window.
		*/
	public int getHeight();
	
	/**
		* Allows the session to retrieve the width of the clients window.
		*
		* @return The width of the clients window.
		*/
	public int getWidth();
	
	/**
		* Sends a beep to the client. Different clients will respond
		* differently to this, most just beep, some flash as well.
		*/
	public void beep();
	
	/**
		* Set whether the client should display a cursor to the user.
		* This is not necessarily supported by the clients terminal.
		*
		* @param vis True to have the client display a cursor.
		*/
	public void setCursorVisibility(boolean vis);
	
	/**
		* Clears the clients terminal window.
		*/
	public void clearScreen();
	
	/**
		* Clears the entire line that the cursor is on.
		*/
	public void clearLine();
	
	/**
		* Moves the cursor to a particular position on the screen.
		* The coordinates given are zero based.
		*
		* @param row The row to move the cursor to.
		* @param column The column to move the cursor to.
		*/
	public void moveCursor(int row, int column);
	
	/**
		* Writes some coloured text information to the clients window.
		*
		* @param text The text to write to the client's window.
		*/
	public void writeText(ColouredString text);
	
	/**
		* Clears the current line and writes the given coloured text to it.
		*
		* @param text The text to replace the current line with.
		*/
	public void writeLine(ColouredString text);
	
	/**
		* Writes a simple string to the clients window.
		*
		* @param text The text to write to the client's window.
		*/
	public void writeText(String text);
	
	/**
		* Clears the current line and writes a simple string to the current line.
		*
		* @param text The text to replace the current line with.
		*/
	public void writeLine(String text);
	
	/**
		* Closes the clients connection.
		*/
	public void close();
}
