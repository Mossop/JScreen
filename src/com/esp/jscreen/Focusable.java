package com.esp.jscreen;

import com.esp.jscreen.events.FocusListener;

public interface Focusable extends FocusListener
{
	public void addFocusListener(FocusListener listener);

	public void removeFocusListener(FocusListener listener);
}
