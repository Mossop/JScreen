package com.esp.jscreen.text;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

/**
 * This class holds a set of text along with colour and formatting information.
 * It implements CharSequence so that regex matching may be used on it.
 */
public abstract class ColouredString implements CharSequence
{
	protected StringBuffer text = new StringBuffer();
	protected List colours = new ArrayList();
	
	/**
   * This method returns an iterator to a set of StringColourPair objects.
   * These objects in order give the text and colour of each part of the ColouredString.
   */
	public Iterator iterator()
	{
		List newlist = new ArrayList();
		ColourInfo colour = new ColourInfo((ColourInfo)colours.get(0));
		ColourInfo nextcolour;
		StringColourPair pair;
		for (int loop=1; loop<colours.size(); loop++)
		{
			nextcolour = new ColourInfo((ColourInfo)colours.get(loop));
			pair = new StringColourPair(colour,text.substring(colour.getOffset(),nextcolour.getOffset()));
			newlist.add(pair);
			colour=nextcolour;
		}
		pair = new StringColourPair(colour,text.substring(colour.getOffset()));
		newlist.add(pair);
		return newlist.iterator();
	}
	
	/**
   * Returns the character at a specified index in the text.
   */
	public char charAt(int index)
	{
		return text.charAt(index);
	}
	
	public ColourInfo colourAt(int index)
	{
		int pos = Collections.binarySearch(colours,new Integer(index));
		if (pos<0)
		{
			pos=-(pos+1)-1;
		}
		ColourInfo colour = new ColourInfo((ColourInfo)colours.get(pos));
		colour.setOffset(index);
		return colour;
	}
	
	/**
   * Returns the length of the text.
   */
	public int length()
	{
		return text.length();
	}
	
	/**
   * Returns a CharSequence of part of the text.
   * No colour information is held in the subsequence.
   */
	public CharSequence subSequence(int start, int end)
	{
		return text.subSequence(start,end);
	}
	
	public ColouredString subString(int start, int end)
	{
		ColouredStringBuffer buffer = new ColouredStringBuffer(this);
		buffer.delete(end,buffer.length());
		buffer.delete(0,start);
		return buffer;
	}
	
	/**
   * Returns the plain text.
   */
	public String toString()
	{
		StringBuffer buffer= new StringBuffer();
		Iterator loop = iterator();
		while (loop.hasNext())
		{
			StringColourPair sc = (StringColourPair)loop.next();
			buffer.append(sc.getColour().getOffset()+" "+sc.toString()+"\n");
		}
		return buffer.toString();
		//return text.toString();
	}
}
