package com.esp.jscreen.widgets;

import com.esp.jscreen.text.ColouredString;

public abstract class TextComponent extends FocusableComponent
{
	public abstract ColouredString getText();
	
	public abstract void setText(ColouredString text);
}
