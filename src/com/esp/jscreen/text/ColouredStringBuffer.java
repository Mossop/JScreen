package com.esp.jscreen.text;

import java.util.Collections;
import java.util.Iterator;

/**
 * This is a ColouredString that can be altered.
 */
public class ColouredStringBuffer extends ColouredString
{
	/**
   * Creates a new ColouredStringBuffer with the contents specified.
   */
	public ColouredStringBuffer(Object newtext)
	{
		this();
		text.append(newtext);
	}
	
	public ColouredStringBuffer(ColourInfo colour, Object newtext)
	{
		this(colour);
		text.append(newtext);
	}
	
	public ColouredStringBuffer()
	{
		super();
		ColourInfo start = new ColourInfo();
		start.setOffset(0);
		insertColourInfo(start);
	}
	
	public ColouredStringBuffer(ColourInfo base)
	{
		super();
		ColourInfo start = new ColourInfo(base);
		start.setOffset(0);
		insertColourInfo(start);
	}
	
	public ColouredStringBuffer(ColouredString copy)
	{
		this();
		append(copy);
	}
	
	public void clear()
	{
		text = new StringBuffer();
		colours.clear();
		ColourInfo colour = new ColourInfo();
		colour.setOffset(0);
		colours.add(colour);
	}
	
	public void setColourAt(int offset, ColourInfo newcolour)
	{
		ColourInfo colour = new ColourInfo(newcolour);
		colour.setOffset(offset);
		insertColourInfo(colour);
	}
	
	/**
   * Inserts a ColouredString into the middle of this one.
   */
	public void insert(int offset, ColouredString newtext)
	{
		if (offset==text.length())
		{
			append(newtext);
		}
		else
		{
			insertColourInfo(colourAt(offset));
			advanceColourOffsets(offset,newtext.length());
			Iterator loop = newtext.iterator();
			while (loop.hasNext())
			{
				StringColourPair sc = (StringColourPair)loop.next();
				sc.getColour().setOffset(offset);
				insertColourInfo(sc.getColour());
				text.insert(offset,sc.toString());
				offset+=sc.toString().length();
			}
		}
	}
	
	public void append(ColouredString newtext)
	{
		Iterator loop = newtext.iterator();
		while (loop.hasNext())
		{
			StringColourPair sc = (StringColourPair)loop.next();
			sc.getColour().setOffset(text.length());
			insertColourInfo(sc.getColour());
			text.append(sc.toString());
		}
	}
	
	public void insert(int offset, Object newtext, ColourInfo colour)
	{
		int len = newtext.toString().length();
		insert(offset,newtext);
		colour.setOffset(offset);
		setColourAt(offset+len,colourAt(len));
		setColourAt(offset,colour);
	}
	
	/**
   * Inserts some object into the text.
   */
	public void insert(int offset, Object newtext)
	{
		if (offset==text.length())
		{
			append(newtext);
		}
		else
		{
			int inc=text.length();
			text.insert(offset,newtext);
			inc=text.length()-inc;
			if (offset==0)
			{
				offset++;
			}
			advanceColourOffsets(offset+1,inc);
		}
	}
	
	public void append(Object newtext, ColourInfo colour)
	{
		int offset = text.length();
		text.append(newtext);
		colour.setOffset(offset);
		setColourAt(offset,colour);
	}
	
	public void append(Object newtext)
	{
		text.append(newtext);
	}
	
	/**
	 * Deletes text from this object.
	 */
	public void delete(int offset, int finish)
	{
		int end = Collections.binarySearch(colours,new Integer(offset));
		int pos = Collections.binarySearch(colours,new Integer(finish-1));
		if (pos<0)
		{
			pos=-(pos+1)-1;
		}
		if (end<0)
		{
			end=-(end+1);
		}
		if (pos>=end)
		{
			insertColourInfo(colourAt(finish));
		}
		while (pos>=end)
		{
			colours.remove(pos);
			pos--;
		}
		advanceColourOffsets(finish,offset-finish);
		text.delete(offset,finish);
	}
		
	/**
   * Every ColourInfo offset after a point in the text gets advanced by the given increment.
   * Used when inserting text into this object.
   */
	protected void advanceColourOffsets(int offset, int increment)
	{
		int pos = Collections.binarySearch(colours,new Integer(offset));
		if (pos<0)
		{
			pos=-(pos+1);
		}
		ColourInfo ci;
		for (int loop=pos; loop<colours.size(); loop++)
		{
			ci = (ColourInfo)colours.get(loop);
			ci.setOffset(ci.getOffset()+increment);
		}
	}
	
	/**
   * Helper method to insert the colour information into the correct place.
   */
	protected void insertColourInfo(ColourInfo colour)
	{
		int pos = Collections.binarySearch(colours,colour);
		if (pos>=0)
		{
			colours.remove(pos);
			colours.add(pos,colour);
		}
		else
		{
			colours.add(-(pos+1),colour);
		}
	}
}
