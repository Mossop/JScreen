package com.esp.jscreen.components;


public class Area {
	private int top;
	private int left;
	private int height;
	private int width;
	
	public Area() 
	{
		top=0;
		left=0;
		height=0;
		width=0;
	}

	public void setTop(int top)
	{
		this.top = top; 
	}

	public void setLeft(int left)
	{
		this.left = left; 
	}

	public void setHeight(int height)
	{
		this.height = height; 
	}

	public void setWidth(int width)
	{
		this.width = width; 
	}
	
	public int getTop()
	{
		return (this.top); 
	}

	public int getLeft()
	{
		return (this.left); 
	}

	public int getHeight()
	{
		return (this.height); 
	}

	public int getWidth()
	{
		return (this.width); 
	}	
	
	public String toString()
	{
		return left+","+top+" "+width+"x"+height;
	}
}
