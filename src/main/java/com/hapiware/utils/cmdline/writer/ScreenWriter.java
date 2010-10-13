package com.hapiware.utils.cmdline.writer;

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
	private final int _screenWidth;
	private HeadingLevel _headingLevelForListItems;
	
	public ScreenWriter(int screenWidth)
	{
		_screenWidth = screenWidth;
	}

	public void h1(String text)
	{
		System.out.println(cut(text));
	}

	public void h2(String text)
	{
		System.out.println(cut(tab(HeadingLevel.H1) + text));
	}

	public void h3(String text)
	{
		System.out.println(cut(tab(HeadingLevel.H2) + text));
	}

	public void h4(String text)
	{
		System.out.println(cut(tab(HeadingLevel.H3) + text));
	}

	public void h5(String text)
	{
		System.out.println(cut(tab(HeadingLevel.H4) + text));
	}

	public void line(HeadingLevel headingLevel, String text)
	{
		System.out.println(cut(tab(headingLevel) + text));
	}

	public void paragraph(HeadingLevel headingLevel, String text)
	{
		Util.write(text, TAB_SIZE * (headingLevel.ordinal() + 1), _screenWidth, System.out);
		System.out.println();
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
		System.out.println(cut(tab(_headingLevelForListItems) + "* " + text));
	}

	public void listEnd()
	{
		System.out.println();
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
