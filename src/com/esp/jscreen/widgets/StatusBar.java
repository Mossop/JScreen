package com.esp.jscreen.widgets;

import com.esp.jscreen.Component;
import com.esp.jscreen.Rectangle;
import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColouredStringBuffer;

public class StatusBar extends Label
{
	public StatusBar()
	{
		super();
		ColourInfo colour = new ColourInfo();
		colour.setBackground(ColourInfo.COLOUR_WHITE);
		colour.setForeground(ColourInfo.COLOUR_GREEN);
		setBackgroundColour(colour);
	}
}
