package com.esp.jscreen.text;

/**
 * Holds a single string and the colour of that string.
 * Used for splitting a ColouredString into fragments.
 */
public class StringColourPair
{
	private ColourInfo colour;
	private StringBuffer text;
	
	/**
   * Creates the object.
   */
	public StringColourPair(ColourInfo colour, StringBuffer text)
	{
		this.colour=colour;
		this.text=text;
	}
	
	/**
   * Creates the object.
   */
	public StringColourPair(ColourInfo colour, String text)
	{
		this.colour=colour;
		this.text=new StringBuffer(text);
	}
	
	/**
   * Returns the text.
   */
	public StringBuffer getString()
	{
		return text;
	}
	
	/**
   * Returns the text.
   */
	public String toString()
	{
		return text.toString();
	}
	
	/**
   * Returns the colour of the text.
   */
	public ColourInfo getColour()
	{
		return colour;
	}
}
