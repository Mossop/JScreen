package com.esp.jscreen.events;

import java.util.Date;

/**
	* The EventObject is based on the class of the same name in the
	* java.awt.event package. It has one addition, a date to indicate when
	* the event occured. If a clone of the event is made, the cloned event
	* will have a date reflected when it was cloned.
	*/
public abstract class EventObject implements Cloneable
{
	/**
		* The object that triggered the event.
		*/
	protected Object source;
	/**
		* The date the event occured.
		*/
	private Date date;

	/**
		* Creates the event.
		*/	
	public EventObject(Object source)
	{
		this.source=source;
		date = new Date();
	}
	
	/**
		* Returns the object that caused the event.
		*/
	public Object getSource()
	{
		return source;
	}
	
	/**
		*	Returns the date that the event occured.
		*/
	public Date getDate()
	{
		return date;
	}
	
	/**
		* Sets the date that the event occured.
		*/
	protected void setDate(Date newdate)
	{
		date=newdate;
	}
	
	/**
		* Clones the event.
		*/
	public Object clone()
	{
		try
		{
			EventObject newevent = (EventObject)super.clone();
			newevent.setDate(new Date());
			return newevent;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
		* Creates a printable representation of the event.
		*/
	public abstract String toString();
}
