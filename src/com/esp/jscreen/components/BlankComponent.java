package com.esp.jscreen.components;

import com.esp.jscreen.text.ColouredStringBuffer;
import com.esp.jscreen.text.ColouredString;

public class BlankComponent extends Component
{
	public BlankComponent(Container parent)
	{
		super(parent);
	}
	
	public ColouredString getLine(int line)
	{
		ColouredStringBuffer buffer = new ColouredStringBuffer();
		for (int loop=0; loop<width; loop++)
		{
			buffer.append(" ");
		}
		return buffer;
	}
}
