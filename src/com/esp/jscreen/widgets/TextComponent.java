package com.esp.jscreen.widgets;

import com.esp.jscreen.text.ColouredString;

public abstract class TextComponent extends FocusableComponent
{
	public abstract String getText();

	public abstract void setText(String text);
}
