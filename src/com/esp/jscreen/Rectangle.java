package com.esp.jscreen;

/**
	* Like the java.awt.Rectangle class, but with less
	* functionality.
	*/
public class Rectangle
{
	private int left;
	private int top;
	private int right;
	private int bottom;

	public Rectangle()
	{
		this(0,0,0,0);
	}
	
	public Rectangle(Rectangle base)
	{
		this(base.getLeft(),base.getTop(),base.getWidth(),base.getHeight());
	}
	
	public Rectangle(int left,int top,int width, int height)
	{
		this.left=left;
		this.top=top;
		this.right=left+width;
		this.bottom=top+height;
	}
	
	public void translate(int xshift, int yshift)
	{
		setLeft(getLeft()+xshift);
		setRight(getRight()+xshift);
		setTop(getTop()+yshift);
		setBottom(getBottom()+yshift);
	}
	
	public Rectangle union(Rectangle other)
	{
		Rectangle newrect = new Rectangle();
		newrect.setLeft(Math.max(getLeft(),other.getLeft()));
		newrect.setTop(Math.max(getTop(),other.getTop()));
		newrect.setRight(Math.min(getRight(),other.getRight()));
		newrect.setBottom(Math.min(getBottom(),other.getBottom()));
		if (newrect.getArea()<=0)
		{
			return new Rectangle();
		}
		else
		{
			return newrect;
		}
	}
	
	public Rectangle[] subtract(Rectangle other)
	{
		Rectangle contained = union(other);
		if (contained.getArea()==0)
		{
			return new Rectangle[0];
		}
		else
		{
			Rectangle[] rects = new Rectangle[4];

			rects[0] = new Rectangle(this);
			rects[0].setBottom(contained.getTop());

			rects[1] = new Rectangle(this);
			rects[1].setTop(contained.getTop());
			rects[1].setRight(contained.getLeft());
			rects[1].setBottom(contained.getBottom());

			rects[2] = new Rectangle(this);
			rects[2].setTop(contained.getTop());
			rects[2].setLeft(contained.getRight());
			rects[2].setBottom(contained.getBottom());

			rects[3] = new Rectangle(this);
			rects[3].setTop(contained.getBottom());

			int count=0;
			for (int loop=0; loop<4; loop++)
			{
				if (rects[loop].getArea()>0)
				{
					count++;
				}
			}

			Rectangle[] finalrects = new Rectangle[count];
			count=0;
			for (int loop=0; loop<4; loop++)
			{
				if (rects[loop].getArea()>0)
				{
					finalrects[count]=rects[loop];
					count++;
				}
			}
			return finalrects;
		}
	}
	
	public void setSize(int width, int height)
	{
		setRight(width+getLeft());
		setBottom(height+getTop());
	}
	
	public void setOrigin(int x, int y)
	{
		setLeft(x);
		setRight(y);
	}
	
	public int getWidth()
	{
		return getRight()-getLeft();
	}

	public void setWidth(int value)
	{
		setRight(value+getLeft());
	}
	
	public int getHeight()
	{
		return getBottom()-getTop();
	}
	
	public void setHeight(int value)
	{
		setBottom(value+getTop());
	}
	
	public int getArea()
	{
		return getWidth()*getHeight();
	}
	
	public Object clone()
	{
		return new Rectangle(this);
	}
	
	public int getLeft()
	{
		return left;
	}

	public void setLeft(int value)
	{
		left=value;
	}
	
	public int getTop()
	{
		return top;
	}

	public void setTop(int value)
	{
		top=value;
	}
	
	public int getRight()
	{
		return right;
	}

	public void setRight(int value)
	{
		right=value;
	}
	
	public int getBottom()
	{
		return bottom;
	}
	
	public void setBottom(int value)
	{
		bottom=value;
	}
	
	public String toString()
	{
		return "Rectangle: [x="+getLeft()+" y="+getTop()+" width="+getWidth()+" height="+getHeight()+"]";
	}
}
