package com.esp.jscreen;

import java.nio.ByteBuffer;
import com.esp.jscreen.text.ColouredString;

public interface OutputTerminalControl
{
	public int getHeight();
	
	public int getWidth();
	
	public void beep();
	
	public void setCursorVisibility(boolean vis);
	
	public void clearScreen();
	
	public void clearLine();
	
	public void moveCursor(int row, int column);
	
	public void writeText(ColouredString text);
	
	public void writeLine(ColouredString text);
	
	public void writeText(String text);
	
	public void writeLine(String text);
	
	public void close();
}
