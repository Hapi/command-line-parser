package com.hapiware.utils.cmdline.writer;

import java.io.PrintStream;

import com.hapiware.utils.cmdline.Util;


/**
 * Headings and list items are one liners only (i.e. if they are longer than defined screen
 * width they are just cut).
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class ScreenWriter
	implements
		Writer
{
	private final static int TAB_SIZE = 4;
	private final static int MIN_SCREEN_WIDTH = 40;
	private final static int MAX_SCREEN_WIDTH = 250;
	private final static PrintStream STREAM = System.out;
	
	private final int _screenWidth;
	private HeadingLevel _headingLevelForListItems;
	private HeadingLevel _headingLevelForCodeLines;
	
	public ScreenWriter(int screenWidth)
	{
		if(screenWidth < MIN_SCREEN_WIDTH || screenWidth > MAX_SCREEN_WIDTH)
			throw
				new IllegalArgumentException(
					screenWidth + " is not an appropriate value for 'screenWidth'. "
						+ " Value must be " + MIN_SCREEN_WIDTH + " - " + MAX_SCREEN_WIDTH + "."
				);
		_screenWidth = screenWidth;
	}

	public void h1(String text)
	{
		STREAM.println(cut(text));
	}

	public void h2(String text)
	{
		STREAM.println(cut(tab(HeadingLevel.H1) + text));
	}

	public void h3(String text)
	{
		STREAM.println(cut(tab(HeadingLevel.H2) + text));
	}

	public void h4(String text)
	{
		STREAM.println(cut(tab(HeadingLevel.H3) + text));
	}

	public void h5(String text)
	{
		STREAM.println(cut(tab(HeadingLevel.H4) + text));
	}

	public void line(HeadingLevel headingLevel, String text)
	{
		STREAM.println(cut(tab(headingLevel) + text));
	}

	public void paragraph(HeadingLevel headingLevel, String text)
	{
		Util.write(text, TAB_SIZE * (headingLevel.ordinal() + 1), _screenWidth, STREAM);
		STREAM.println();
	}

	public String strongBegin()
	{
		return "'";
	}

	public String strongEnd()
	{
		return "'";
	}

	public void listBegin(HeadingLevel headingLevel)
	{
		_headingLevelForListItems = headingLevel;
	}

	public void listItem(String text)
	{
		STREAM.println(cut(tab(_headingLevelForListItems) + "* " + text));
	}

	public void listEnd()
	{
		STREAM.println();
	}

	public void codeBegin(HeadingLevel headingLevel)
	{
		_headingLevelForCodeLines = headingLevel;
	}
	
	public void codeLine(String code)
	{
		line(_headingLevelForCodeLines, code);
	}
	
	public void codeEnd()
	{
		STREAM.println();
	}
	
	public void footer()
	{
		// Does nothing.
	}
	
	public void header()
	{
		// Does nothing.
	}
	
	private static String tab(HeadingLevel headingLevel)
	{
		String tab = "";
		for(int i = 0; i < (headingLevel.ordinal() + 1) * TAB_SIZE; i++)
			tab += " ";
		return tab;
	}

	private String cut(String text)
	{
		return text.length() > _screenWidth ? text.substring(0, _screenWidth - 3) + "..." : text;
	}
}
