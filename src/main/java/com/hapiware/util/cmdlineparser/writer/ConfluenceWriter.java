package com.hapiware.util.cmdlineparser.writer;

import java.io.PrintStream;


/**
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class ConfluenceWriter
	implements
		Writer
{
	private final PrintStream _printStream;

	
	private ConfluenceWriter(PrintStream printStream)
	{
		_printStream = printStream;
	}
	
	@SuppressWarnings("unused")
	private static ConfluenceWriter createForTesting(PrintStream stream)
	{
		return new ConfluenceWriter(stream);
	}
	
	
	public ConfluenceWriter()
	{
		_printStream = System.out;
	}

	public void level1Begin(String text)
	{
		_printStream.println("h1. " + text);
	}

	public void level1End()
	{
		// Does nothing.
	}

	public void level2Begin(String text)
	{
		_printStream.println("h2. " + text);
	}

	public void level2End()
	{
		// Does nothing.
	}

	public void level3Begin(String text)
	{
		_printStream.println("h3. " + text);
	}

	public void level3End()
	{
		// Does nothing.
	}

	public void level4Begin(String text)
	{
		_printStream.println("h4. " + text);
	}

	public void level4End()
	{
		// Does nothing.
	}

	public void level5Begin(String text)
	{
		_printStream.println("h5. " + text);
	}

	public void level5End()
	{
		// Does nothing.
	}

	public void line(Level level, String text)
	{
		_printStream.println(text + " \\\\");
	}

	public void paragraph(Level level, String text)
	{
		_printStream.println(text);
		_printStream.println();
	}

	public String strongBegin()
	{
		return "*";
	}

	public String strongEnd()
	{
		return "*";
	}

	public void listBegin(Level level)
	{
		// Does nothing.
	}

	public void listItem(String text)
	{
		_printStream.println("* " + text);
	}

	public void listEnd()
	{
		_printStream.println();
	}

	public void codeBegin(Level level)
	{
		_printStream.println("{code}");
	}
	
	public void codeLine(String code)
	{
		_printStream.println(code);
	}
	
	public void codeEnd()
	{
		_printStream.println("{code}");
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
