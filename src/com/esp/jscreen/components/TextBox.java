package com.esp.jscreen.components;

import com.esp.jscreen.text.ColourInfo;
import com.esp.jscreen.text.ColouredString;
import com.esp.jscreen.text.ColouredStringBuffer;
import com.esp.jscreen.commands.*;
import java.util.Arrays;

public class TextBox extends Component
{
	private ColouredString title;
	private ColouredStringBuffer text;
	private int displaypos;
	private int cursorpos;
	private boolean leftedged;
	private boolean rightedged;
	private int realwidth;
	private boolean changeattr;
	
	public TextBox(Container parent, ColouredString title)
	{
		super(parent);
		this.title=title;
		displaypos=0;
		cursorpos=0;
		text = new ColouredStringBuffer();
		leftedged=false;
		rightedged=false;
		changeattr=true;
	}
	
	public TextBox(Container parent, String title)
	{
		this(parent,new ColouredStringBuffer(title));
	}
	
	public void setTitle(ColouredString newtitle)
	{
		title=newtitle;
		sizeChanged();
	}
	
	protected void sizeChanged()
	{
		realwidth=width-title.length()-1;
		if ((cursorpos-displaypos)>realwidth)
		{
			displaypos=displaypos+((cursorpos-displaypos)-realwidth);
		}
		if (((text.length()-displaypos)>realwidth)&&((realwidth-(cursorpos-displaypos))<2))
		{
			displaypos=cursorpos-realwidth+2;
		}
		parent.lineUpdate(this,0,getLine(0));
		gotFocus();
	}
	
	public void newCommand(Command command)
	{
		if (command instanceof TextCommand)
		{
			int key = ((TextCommand)command).getKey();
			if (key==TextCommand.TC_NEWLINE)
			{
				text.clear();
				cursorpos=0;
				displaypos=0;
				parent.lineUpdate(this,0,getLine(0));
				gotFocus();
			}
			else if ((key==TextCommand.TC_BACKSPACE)||(key==TextCommand.TC_DELETE))
			{
				if (cursorpos>0)
				{
					text.delete(cursorpos-1,cursorpos);
					if ((cursorpos==(displaypos+2))&&(displaypos>0))
					{
						displaypos--;
					}
					cursorpos--;
					parent.lineUpdate(this,0,getLine(0));
					gotFocus();
				}
				else
				{
					parent.beep();
				}
			}
		}
		else if (command instanceof ControlCommand)
		{
			char key = ((ControlCommand)command).getKey();
			if (changeattr)
			{
				if (key=='C')
				{
					ColourInfo colour = text.colourAt(cursorpos);
					colour.setForeground((colour.getForeground()+1)%ColourInfo.MAX_COLOURS);
					text.setColourAt(cursorpos,colour);
					parent.lineUpdate(this,0,getLine(0));
				}
				else if (key=='B')
				{
					ColourInfo colour = text.colourAt(cursorpos);
					colour.setBackground((colour.getBackground()+1)%ColourInfo.MAX_COLOURS);
					text.setColourAt(cursorpos,colour);
					parent.lineUpdate(this,0,getLine(0));
				}
				else if (key=='U')
				{
					ColourInfo colour = text.colourAt(cursorpos);
					colour.setUnderline(!colour.getUnderline());
					text.setColourAt(cursorpos,colour);
					parent.lineUpdate(this,0,getLine(0));
				}
				else if (key=='F')
				{
					ColourInfo colour = text.colourAt(cursorpos);
					colour.setFlash(!colour.getFlash());
					text.setColourAt(cursorpos,colour);
					parent.lineUpdate(this,0,getLine(0));
				}
				else if (key=='L')
				{
					ColourInfo colour = text.colourAt(cursorpos);
					colour.setBold(!colour.getBold());
					text.setColourAt(cursorpos,colour);
					parent.lineUpdate(this,0,getLine(0));
				}
			}
		}
		else if (command instanceof CursorCommand)
		{
			int key = ((CursorCommand)command).getKey();
			if (key==CursorCommand.CC_CURSORLT)
			{
				if ((cursorpos==(displaypos+2))&&(displaypos>0))
				{
					displaypos--;
					cursorpos--;
					parent.lineUpdate(this,0,getLine(0));
					gotFocus();
				}
				else if (cursorpos>0)
				{
					cursorpos--;
					gotFocus();
				}
				else
				{
					parent.beep();
				}
			}
			else if (key==CursorCommand.CC_CURSORRT)
			{
				if (cursorpos<text.length())
				{
					cursorpos++;
					if ((cursorpos-displaypos)>realwidth)
					{
						displaypos=displaypos+((cursorpos-displaypos)-realwidth);
					}
					if (((text.length()-displaypos)>realwidth)&&((realwidth-(cursorpos-displaypos))<2))
					{
						displaypos=cursorpos-realwidth+2;
					}
					parent.lineUpdate(this,0,getLine(0));
					gotFocus();
				}
				else
				{
					parent.beep();
				}
			}
		}
	}
	
	public void newText(StringBuffer newtext)
	{
		text.insert(cursorpos,newtext);
		cursorpos=cursorpos+newtext.length();
		if ((cursorpos-displaypos)>realwidth)
		{
			displaypos=displaypos+((cursorpos-displaypos)-realwidth);
		}
		if (((text.length()-displaypos)>realwidth)&&((realwidth-(cursorpos-displaypos))<2))
		{
			displaypos=cursorpos-realwidth+2;
		}
		parent.lineUpdate(this,0,getLine(0));
		gotFocus();
	}
	
	protected void gotFocus()
	{
		parent.moveCursor(this,0,(cursorpos-displaypos)+title.length());
	}
	
	public int getMinimumWidth()
	{
		return title.length()+7;
	}
	
	public int getMaximumHeight()
	{
		return 1;
	}
	
	public ColouredString getLine(int line)
	{
		ColouredStringBuffer buffer = new ColouredStringBuffer();
		if (line!=0)
		{
			char[] chars = new char[width];
			Arrays.fill(chars,' ');
			buffer.append(new String(chars));
		}
		else
		{
			if (displaypos>0)
			{
				buffer.append("..",new ColourInfo());
				buffer.append(text.subString(displaypos+2,text.length()));
			}
			else
			{
				buffer.append(text);
			}
			if (buffer.length()>realwidth)
			{
				buffer.delete(realwidth-2,buffer.length());
				buffer.append("..",new ColourInfo());
			}
			else
			{
				char[] chars = new char[realwidth-buffer.length()];
				Arrays.fill(chars,' ');
				buffer.append(new String(chars),new ColourInfo());
			}
			buffer.insert(0,title);
		}
		return buffer;
	}
}
