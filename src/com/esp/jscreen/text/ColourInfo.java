package com.esp.jscreen.text;

/**
 * Holds information about colour and formatting for the ColouredString class.
 */
public class ColourInfo implements Comparable, Cloneable
{
	
	public static final int COLOUR_BLACK = 0;
	public static final int COLOUR_RED = 1;
	public static final int COLOUR_GREEN = 2;
	public static final int COLOUR_YELLOW = 3;
	public static final int COLOUR_BLUE = 4;
	public static final int COLOUR_MAGENTA = 5;
	public static final int COLOUR_CYAN = 6;
	public static final int COLOUR_WHITE = 7;
	public static final int MAX_COLOURS = 8;
	
	/**
   * Holds the offset in the text at which this information takes effect.
   */
	private int offset = 0;
	private boolean underline = false;
	private boolean flash = false;
	private boolean bold = false;
	private int foreground = COLOUR_WHITE;
	private int background = COLOUR_BLACK;
	
	public ColourInfo()
	{
	}
	
	/**
		* Creates this colour based on the given one.
		*/
	public ColourInfo(ColourInfo other)
	{
		offset = other.offset;
		underline = other.underline;
		flash = other.flash;
		bold = other.bold;
		foreground = other.foreground;
		background = other.background;
		assert offset>=0;
	}
	
	/**
		* Sets the offset
		*/
	void setOffset(int newoffset)
	{
		assert newoffset>=0;
		offset=newoffset;
	}
	
	/**
		* Gets the offset
		*/
	int getOffset()
	{
		return offset;
	}
	
	/**
   * Allows us to compare this object to another ColourInfo object or an Integer for
   * indexing purposes.
   */
	public int compareTo(Object obj)
	{
		if (obj instanceof ColourInfo)
		{
			return (new Integer(offset)).compareTo(new Integer(((ColourInfo)obj).getOffset()));
		}
		else if (obj instanceof Integer)
		{
			return (new Integer(offset)).compareTo(obj);
		}
		else
		{
			throw new ClassCastException();
		}
	}

	public void setUnderline(boolean underline)
	{
		this.underline = underline; 
	}

	public void setFlash(boolean flash)
	{
		this.flash = flash; 
	}

	public void setBold(boolean bold)
	{
		this.bold = bold; 
	}

	public void setForeground(int foreground)
	{
		this.foreground = foreground; 
	}

	public void setBackground(int background)
	{
		this.background = background; 
	}

	public boolean getUnderline()
	{
		return (this.underline); 
	}

	public boolean getFlash()
	{
		return (this.flash); 
	}

	public boolean getBold()
	{
		return (this.bold); 
	}

	public int getForeground()
	{
		return (this.foreground); 
	}

	public int getBackground()
	{
		return (this.background); 
	}
}
