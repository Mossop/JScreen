package com.esp.jscreen.widgets;

import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColouredStringBuffer;
import com.esp.jscreen.events.KeyEvent;
import com.esp.jscreen.events.ControlKeyEvent;
import com.esp.jscreen.events.CursorKeyEvent;
import com.esp.jscreen.Rectangle;

public class TextField extends TextComponent
{
	private ColouredStringBuffer text;
	private int startpos;
	private int cursorpos;
	private int scrolllen;
	
	public TextField()
	{
		text = new ColouredStringBuffer();
		startpos=0;
		cursorpos=0;
		scrolllen=5;
		setCursorPos(0,0);
	}
	
	public int getMaximumHeight()
	{
		return 1;
	}
	
	public void setText(ColouredString text)
	{
		text = new ColouredStringBuffer(text);
	}
	
	public ColouredString getText()
	{
		return text;
	}
	
	public boolean keyPressed(KeyEvent e)
	{
		if (e.isAlphaNumeric())
		{
			text.insert(startpos+cursorpos,new Character(e.getKey()));
			if ((cursorpos+1)>=getWidth())
			{
				cursorpos++;
				while (cursorpos>=getWidth())
				{
					startpos=startpos+scrolllen;
					cursorpos=cursorpos-scrolllen;
				}
				update(new Rectangle(0,0,getWidth(),1));						
			}
			else
			{
				int endpos=Math.min(text.length()-startpos,getWidth());
				update(new Rectangle(cursorpos,0,endpos-cursorpos,1));
				cursorpos++;
			}
			setCursorPos(cursorpos,0);
		}
		else if (e instanceof ControlKeyEvent)
		{
		}
		else if (e instanceof CursorKeyEvent)
		{
			if (e.getKey()==CursorKeyEvent.CURSORLT)
			{
				if ((cursorpos+startpos)>0)
				{
					cursorpos--;
					if (cursorpos<0)
					{
						while (cursorpos<0)
						{
							startpos=startpos-scrolllen;
							cursorpos=cursorpos+scrolllen;
						}
						int endpos=Math.min(text.length()-startpos,getWidth());
						update(new Rectangle(0,0,endpos,1));
					}
					setCursorPos(cursorpos,0);
				}
			}
			else if (e.getKey()==CursorKeyEvent.CURSORRT)
			{
				if ((cursorpos+startpos)<text.length())
				{
					cursorpos++;
					if (cursorpos>=getWidth())
					{
						while (cursorpos>=getWidth())
						{
							startpos=startpos+scrolllen;
							cursorpos=cursorpos-scrolllen;
						}
						update(new Rectangle(0,0,getWidth(),1));						
					}
					setCursorPos(cursorpos,0);
				}
			}
		}
		else if ((e.getKey()==KeyEvent.BACKSPACE)||(e.getKey()==KeyEvent.DELETE))
		{
			if ((cursorpos+startpos)>0)
			{
				cursorpos--;
				text.delete(cursorpos+startpos,cursorpos+startpos+1);
				if (cursorpos<0)
				{
					while (cursorpos<0)
					{
						startpos=startpos-scrolllen;
						cursorpos=cursorpos+scrolllen;
					}
					int endpos=Math.min(text.length()-startpos,getWidth());
					update(new Rectangle(0,0,endpos,1));
				}
				else
				{
					int endpos=Math.min(text.length()-startpos+1,getWidth());
					update(new Rectangle(cursorpos,0,endpos-cursorpos,1));	
				}
			}
			setCursorPos(cursorpos,0);
		}
		
		return super.keyPressed(e);
	}
	
	public ColouredString getLine(int x, int y, int width)
	{
		ColouredStringBuffer result = new ColouredStringBuffer(getBackgroundColour());
		if ((x<(text.length()-startpos))&&(y==0))
		{
			ColouredString line = text.subString(x+startpos,text.length());
			if (line.length()>width)
			{
				result.append(line.subString(0,width));
			}
			else
			{
				result.append(line);
			}
		}
		while (result.length()<width)
		{
			result.append(" ");
		}
		return result;
	}
}
