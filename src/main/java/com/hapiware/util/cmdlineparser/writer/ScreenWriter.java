package com.hapiware.util.cmdlineparser.writer;

import java.io.PrintStream;

import com.hapiware.util.cmdlineparser.Util;


/**
 * System property {@code screenwidth.default}.
 * 
 * 
 * Headings and list items are one liners only (i.e. if they are longer than defined screen
 * width they are just cut off).
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
	private static final String SCREEN_WIDTH_PROPERTY = "screenwidth.default";
	private static final int DEFAULT_SCREEN_WIDTH;
	
	static {
		int screenWidth = 100;
		try {
			screenWidth = Integer.parseInt(System.getProperty(SCREEN_WIDTH_PROPERTY));
		}
		catch(Throwable ignore) {
			// Does nothing.
		}
		finally {
			DEFAULT_SCREEN_WIDTH = screenWidth;
		}
	}
	
	private final int _screenWidth;
	private Level _levelForListItems;
	private Level _levelForCodeLines;
	
	public ScreenWriter()
	{
		this(DEFAULT_SCREEN_WIDTH);
	}
	
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

	public void level1Begin(String text)
	{
		STREAM.println(cut(text));
	}
	
	public void level1End()
	{
		// Does nothing.
	}

	public void level2Begin(String text)
	{
		STREAM.println(cut(tab(Level.L1) + text));
	}

	public void level2End()
	{
		// Does nothing.
	}

	public void level3Begin(String text)
	{
		STREAM.println(cut(tab(Level.L2) + text));
	}

	public void level3End()
	{
		// Does nothing.
	}

	public void level4Begin(String text)
	{
		STREAM.println(cut(tab(Level.L3) + text));
	}

	public void level4End()
	{
		// Does nothing.
	}

	public void level5Begin(String text)
	{
		STREAM.println(cut(tab(Level.L4) + text));
	}

	public void level5End()
	{
		// Does nothing.
	}

	public void line(Level level, String text)
	{
		STREAM.println(cut(tab(level) + text));
	}

	public void paragraph(Level level, String text)
	{
		Util.write(text, TAB_SIZE * (level.ordinal() + 1), _screenWidth, STREAM);
		STREAM.println();
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

	public void listBegin(Level level)
	{
		_levelForListItems = level;
	}

	public void listItem(String text)
	{
		STREAM.println(cut(tab(_levelForListItems) + "* " + text));
	}

	public void listEnd()
	{
		STREAM.println();
	}

	public void codeBegin(Level level)
	{
		_levelForCodeLines = level;
	}
	
	public void codeLine(String code)
	{
		line(_levelForCodeLines, code);
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
	
	private static String tab(Level level)
	{
		String tab = "";
		for(int i = 0; i < (level.ordinal() + 1) * TAB_SIZE; i++)
			tab += " ";
		return tab;
	}

	private String cut(String text)
	{
		return text.length() > _screenWidth ? text.substring(0, _screenWidth - 3) + "..." : text;
	}
}
