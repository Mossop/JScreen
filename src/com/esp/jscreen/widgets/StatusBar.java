package com.esp.jscreen.widgets;

import com.esp.jscreen.Component;
import com.esp.jscreen.Rectangle;
import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColouredStringBuffer;

public class StatusBar extends Component
{
	private String text;
	private ColourInfo colour;
	
	public StatusBar()
	{
		super();
		text="";
		colour = new ColourInfo();
		colour.setBackground(ColourInfo.COLOUR_WHITE);
		colour.setForeground(ColourInfo.COLOUR_GREEN);
	}
	
	public void setColour(ColourInfo colour)
	{
		this.colour=new ColourInfo(colour);
		update();
	}
	
	public void setText(String newtext)
	{
		int max = Math.max(newtext.length(),text.length());
		text=newtext;
		update(new Rectangle(0,0,max,1));
	}
	
	public String getText()
	{
		return text;
	}
	
	public int getMaximumHeight()
	{
		return 1;
	}

	public ColouredString getLine(int x, int y, int width)
	{
		ColouredStringBuffer result = new ColouredStringBuffer(colour);
		if ((x<text.length())&&(y==0))
		{
			String line = text.substring(x);
			if (line.length()>width)
			{
				result.append(line.substring(0,width));
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
