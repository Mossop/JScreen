package com.esp.jscreen.text;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class MultiLineBuffer
{
	List lines;
	int wrappoint;
	Map lineend;
	
	public MultiLineBuffer()
	{
		lines = new ArrayList();
		wrappoint=-1;
		lineend = new HashMap();
	}
	
	public MultiLineBuffer(String newtext)
	{
		this();
		int pos=newtext.indexOf("\n");
		int oldpos=0;
		while (pos>=0)
		{
			ColouredString thisline = new ColouredStringBuffer(newtext.substring(oldpos,pos));
			lineend.put(thisline,new Boolean(true));
			lines.add(thisline);
			oldpos=pos+1;
			pos=newtext.indexOf("\n",oldpos);
		}
		if (pos<newtext.length())
		{
			ColouredString thisline = new ColouredStringBuffer(newtext.substring(pos));
			lineend.put(thisline,new Boolean(true));
			lines.add(thisline);
		}
	}
	
	public MultiLineBuffer(ColouredString copy)
	{
		this();
		int pos=copy.indexOf("\n");
		int oldpos=0;
		while (pos>=0)
		{
			ColouredString thisline = copy.subString(oldpos,pos);
			lineend.put(thisline,new Boolean(true));
			lines.add(thisline);
			oldpos=pos+1;
			pos=copy.indexOf("\n",oldpos);
		}
		if (pos<copy.length())
		{
			ColouredString thisline = copy.subString(pos,copy.length());
			lineend.put(thisline,new Boolean(true));
			lines.add(thisline);
		}
	}
	
	public void overlay(int x, int y, MultiLineBuffer buffer)
	{
		while (lines.size()<y)
		{
			ColouredStringBuffer newline = new ColouredStringBuffer();
			lines.add(newline);
			lineend.put(newline,new Boolean(true));
		}
		ColouredStringBuffer thisline;
		ColouredStringBuffer newline;
		for (int loop=0; loop<buffer.getLineCount(); loop++)
		{
			newline=(ColouredStringBuffer)buffer.getLine(loop);
			if ((loop+y)>=lines.size())
			{
				thisline = new ColouredStringBuffer();
				lines.add(thisline);
				lineend.put(thisline,new Boolean(true));
			}
			else
			{
				thisline=(ColouredStringBuffer)lines.get(y+loop);
			}
			while (thisline.length()<=x)
			{
				thisline.append(" ");
			}
			if (thisline.length()>x)
			{
				int end = thisline.length();
				if ((newline.length()+x)<end)
				{
					end=newline.length()+x;
				}
				thisline.delete(x,end);
			}
			thisline.insert(x,newline);
		}
	}
	
	public int getLineCount()
	{
		return lines.size();
	}
	
	public ColouredString getLine(int line)
	{
		return (ColouredString)lines.get(line);
	}
}
