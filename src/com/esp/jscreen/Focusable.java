package com.esp.jscreen;

import com.esp.jscreen.events.FocusListener;

/**
	* Any component that can be focussed must implement this interface.
	*/
public interface Focusable extends FocusListener
{
	/**
		* Registers a focuslistener with the component.
		*/
	public void addFocusListener(FocusListener listener);

	/**
		* Deregisters a focuslistener with the component.
		*/
	public void removeFocusListener(FocusListener listener);
}
