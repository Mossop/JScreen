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
		if (oldpos<newtext.length())
		{
			ColouredString thisline = new ColouredStringBuffer(newtext.substring(oldpos));
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
		if (oldpos<copy.length())
		{
			ColouredString thisline = copy.subString(oldpos,copy.length());
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
	
	public void unWordWrap()
	{
		int loop=lines.size()-2;
		ColouredStringBuffer nextline;
		ColouredStringBuffer thisline;
		while (loop>=0)
		{
			if (!((Boolean)lineend.get(lines.get(loop))).booleanValue())
			{
				thisline=(ColouredStringBuffer)lines.get(loop);
				nextline=(ColouredStringBuffer)lines.get(loop+1);
				thisline.append(nextline);
				lines.remove(loop+1);
			}
			loop--;
		}
		wrappoint=-1;
	}
	
	public void wordWrap(int point)
	{
		if (wrappoint!=-1)
		{
			unWordWrap();
		}
		if (point>=0)
		{
			int loop=0;
			ColouredStringBuffer thisline;
			ColouredStringBuffer nextline;
			while (loop<lines.size())
			{
				thisline=(ColouredStringBuffer)lines.get(loop);
				if (thisline.length()>point)
				{
					int pos=thisline.lastIndexOf(" ",point-1);
					if (pos==-1)
					{
						pos=thisline.indexOf(" ",point);
					}
					if (pos>=0)
					{
						nextline=(ColouredStringBuffer)thisline.subString(pos+1,thisline.length());
						thisline.delete(pos+1,thisline.length());
						lineend.put(nextline,new Boolean(true));
						lineend.put(thisline,new Boolean(false));
						lines.add(loop+1,nextline);
					}
				}
				loop++;
			}
			wrappoint=point;
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
	
	public String toString()
	{
		StringBuffer output = new StringBuffer();
		for (int loop=0; loop<lines.size(); loop++)
		{
			output.append(lines.get(loop)+"\n");
		}
		return output.toString();
	}
}
