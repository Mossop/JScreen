package com.esp.jscreen.widgets;

import com.esp.jscreen.Component;
import com.esp.jscreen.Rectangle;
import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColouredStringBuffer;

public class Label extends Component
{
	private ColouredString text;
	
	public Label()
	{
		this("");
	}
	
	public Label(String text)
	{
		this(new ColouredStringBuffer(text));
	}
	
	public Label(ColouredString text)
	{
		this.text=text;
	}
	
	public void setText(String text)
	{
		setText(new ColouredStringBuffer(text));
	}
	
	public void setText(ColouredString newtext)
	{
		int max = Math.max(newtext.length(),text.length());
		text=newtext;
		update(new Rectangle(0,0,max,1));
	}
	
	public ColouredString getText()
	{
		return text;
	}
	
	public int getMaximumHeight()
	{
		return 1;
	}

	public ColouredString getLine(int x, int y, int width)
	{
		ColouredStringBuffer result = new ColouredStringBuffer(getPalette().getColour("LABEL"));
		if ((x<text.length())&&(y==0))
		{
			ColouredString line = text.subString(x,text.length());
			if (line.length()>width)
			{
				result.append(line.subString(0,width));
			}
			else
			{
				result.append(line);
			}
		}
		while (result.length()<width)
		{
			result.append(" ");
		}
		return result;
	}
}
