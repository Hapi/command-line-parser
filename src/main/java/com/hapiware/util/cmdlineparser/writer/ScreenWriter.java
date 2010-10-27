package com.hapiware.util.cmdlineparser.writer;

import java.io.PrintStream;

import com.hapiware.util.cmdlineparser.Util;


/**
 * System property {@code screenwidth}.
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
	private static final String SCREEN_WIDTH_PROPERTY = "screenwidth";
	private static final int DEFAULT_SCREEN_WIDTH = 100;
	
	private final PrintStream _printStream;
	private final int _screenWidth;
	private Level _levelForListItems;
	private Level _levelForCodeLines;
	
	
	/**
	 * This constructor is for testing purpouses only.
	 * 
	 * @see #createForTesting(PrintStream, int)
	 */
	private ScreenWriter(PrintStream stream, int screenWidth)
	{
		_printStream = stream;
		_screenWidth = screenWidth;
	}
	
	@SuppressWarnings("unused")
	private static ScreenWriter createForTesting(PrintStream stream, int screenWidth)
	{
		return new ScreenWriter(stream, screenWidth);
	}
	
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
		try {
			screenWidth = Integer.parseInt(System.getProperty(SCREEN_WIDTH_PROPERTY));
		}
		catch(Throwable ignore) {
			// Does nothing.
		}
		_screenWidth = screenWidth;
		_printStream = System.out;
	}

	public void level1Begin(String text)
	{
		Util.write(text, 0, _screenWidth, _printStream);
		_printStream.println();
	}
	
	public void level1End()
	{
		// Does nothing.
	}

	public void level2Begin(String text)
	{
		Util.write(text, TAB_SIZE * (Level.L1.ordinal()), _screenWidth, _printStream);
		_printStream.println();
	}

	public void level2End()
	{
		// Does nothing.
	}

	public void level3Begin(String text)
	{
		Util.write(text, TAB_SIZE * (Level.L2.ordinal()), _screenWidth, _printStream);
		_printStream.println();
	}

	public void level3End()
	{
		// Does nothing.
	}

	public void level4Begin(String text)
	{
		Util.write(text, TAB_SIZE * (Level.L3.ordinal()), _screenWidth, _printStream);
		_printStream.println();
	}

	public void level4End()
	{
		// Does nothing.
	}

	public void level5Begin(String text)
	{
		Util.write(text, TAB_SIZE * (Level.L4.ordinal()), _screenWidth, _printStream);
		_printStream.println();
	}

	public void level5End()
	{
		// Does nothing.
	}

	public void line(Level level, String text)
	{
		Util.write(text, TAB_SIZE * (level.ordinal()), _screenWidth, _printStream);
		_printStream.println();
	}

	public void paragraph(Level level, String text)
	{
		Util.write(text, TAB_SIZE * (level.ordinal()), _screenWidth, _printStream);
		_printStream.println();
		_printStream.println();
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
		int tabSize = TAB_SIZE * (_levelForListItems.ordinal());
		Util.write("* " + text, tabSize, tabSize + 2, _screenWidth, _printStream);
		_printStream.println();
	}

	public void listEnd()
	{
		_printStream.println();
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
		_printStream.println();
	}
	
	public void footer()
	{
		// Does nothing.
	}
	
	public void header()
	{
		// Does nothing.
	}
}
