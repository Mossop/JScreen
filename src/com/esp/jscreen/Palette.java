package com.esp.jscreen;

import java.util.HashMap;
import java.util.Map;
import com.esp.jscreen.text.ColourInfo;

/**
	* This class holds a set of colours for a frame. Components
	* will take their colours from this by default. Colours are stored against
	* a string.
	*/
public class Palette
{
	/**
	 * Holds the colours agains a string keyword.
	 */
	private Map colours;
	/**
	 * A default colour if  something asks for a colour that soesnt exist.
	 */
	private ColourInfo defaultcolour;

	/**
		* Creates the default palette.
		*/
	public Palette()
	{
		colours = new HashMap();
	}

	public void setDefault(ColourInfo colour)
	{
		defaultcolour = new ColourInfo(colour);
	}

	public ColourInfo getColour(String key)
	{
		Object obj = colours.get(key);
		if ((obj!=null)&&(obj instanceof ColourInfo))
		{
			return (ColourInfo)obj;
		}
		else
		{
			System.err.println("Unknown colour - "+key);
			return defaultcolour;
		}
	}

	public void setColour(String key, ColourInfo colour)
	{
		colours.put(key,colour);
	}
}
