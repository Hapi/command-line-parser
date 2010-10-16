package com.hapiware.utils.cmdline.writer;

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
	private final static PrintStream STREAM = System.out;
	
	
	public ConfluenceWriter()
	{
		// Does nothing.
	}

	public void level1Begin(String text)
	{
		STREAM.println("h1. " + text);
	}

	public void level1End()
	{
		// Does nothing.
	}

	public void level2Begin(String text)
	{
		STREAM.println("h2. " + text);
	}

	public void level2End()
	{
		// Does nothing.
	}

	public void level3Begin(String text)
	{
		STREAM.println("h3. " + text);
	}

	public void level3End()
	{
		// Does nothing.
	}

	public void level4Begin(String text)
	{
		STREAM.println("h4. " + text);
	}

	public void level4End()
	{
		// Does nothing.
	}

	public void level5Begin(String text)
	{
		STREAM.println("h5. " + text);
	}

	public void level5End()
	{
		// Does nothing.
	}

	public void line(Level level, String text)
	{
		STREAM.println(text + " \\\\");
	}

	public void paragraph(Level level, String text)
	{
		STREAM.println(text);
		STREAM.println();
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
		STREAM.println("* " + text);
	}

	public void listEnd()
	{
		STREAM.println();
	}

	public void codeBegin(Level level)
	{
		STREAM.println("{code}");
	}
	
	public void codeLine(String code)
	{
		STREAM.println(code);
	}
	
	public void codeEnd()
	{
		STREAM.println("{code}");
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
}
