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
	private Map colours;

	/**
		* Creates the default palette.
		*/
	public Palette()
	{
		colours = new HashMap();
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
			return null;
		}
	}

	public void setColour(String key, ColourInfo colour)
	{
		colours.put(key,colour);
	}
}
