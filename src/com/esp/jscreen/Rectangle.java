package com.esp.jscreen;

/**
	* Like the java.awt.Rectangle class, but with less
	* functionality. All coordinates and sizes are integers only.
	*/
public class Rectangle
{
	/**
		* The left coordinate
		*/
	private int left;
	/**
		* The top coordinate
		*/
	private int top;
	/**
		* The right coordinate
		*/
	private int right;
	/**
		* The bottom coordinate
		*/
	private int bottom;

	/**
		* Creates a basic rectangle with no size.
		*/
	public Rectangle()
	{
		this(0,0,0,0);
	}
	
	/**
		* Creates a new rectangle based on the given rectangle.
		*/
	public Rectangle(Rectangle base)
	{
		this(base.getLeft(),base.getTop(),base.getWidth(),base.getHeight());
	}
	
	/**
		* Creates a rectangle with the given information.
		*/
	public Rectangle(int left,int top,int width, int height)
	{
		this.left=left;
		this.top=top;
		this.right=left+width-1;
		this.bottom=top+height-1;
	}
	
	/**
		* Translates the rectangle.
		*/
	public void translate(int xshift, int yshift)
	{
		setLeft(getLeft()+xshift);
		setRight(getRight()+xshift);
		setTop(getTop()+yshift);
		setBottom(getBottom()+yshift);
	}
	
	/**
		* Returns a rectangle that is the union of this and the given
		* rectangle.
		*/
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
	
	/**
		* Returns a number of rectangles (up to 4) that describe the area
		* covered by this rectangle but not by the given rectangle.
		*/
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
	
	/**
		* Sets the size of the rectangle.
		*/
	public void setSize(int width, int height)
	{
		setWidth(width);
		setHeight(height);
	}
	
	/**
		* Moves the top left of the rectangle to the given place without
		* affecting the size.
		*/
	public void setOrigin(int x, int y)
	{
		translate(x-getLeft(),y-getTop());
	}
	
	public int getWidth()
	{
		return getRight()-getLeft()+1;
	}

	public void setWidth(int value)
	{
		setRight(value+getLeft()-1);
	}
	
	public int getHeight()
	{
		return getBottom()-getTop()+1;
	}
	
	public void setHeight(int value)
	{
		setBottom(value+getTop()-1);
	}
	
	/**
		* Returns the area that the rectangle covers.
		*/
	public int getArea()
	{
		return getWidth()*getHeight();
	}
	
	/**
		* Clones this rectangle quickly.
		*/
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
	
	/**
		* Returns a simple description of this rectangle.
		*/
	public String toString()
	{
		return "Rectangle: [x="+getLeft()+" y="+getTop()+" width="+getWidth()+" height="+getHeight()+"]";
	}
}
