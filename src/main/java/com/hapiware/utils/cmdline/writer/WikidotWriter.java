package com.hapiware.utils.cmdline.writer;

import java.io.PrintStream;


/**
 * Headings and list items are one liners only (i.e. if they are longer than defined screen
 * width they are just cut).
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class WikidotWriter
	implements
		Writer
{
	private final static PrintStream STREAM = System.out;
	
	
	public WikidotWriter()
	{
		// Does nothing.
	}

	public void h1(String text)
	{
		STREAM.println("+ " + text);
	}

	public void h2(String text)
	{
		STREAM.println("++ " + text);
	}

	public void h3(String text)
	{
		STREAM.println("+++ " + text);
	}

	public void h4(String text)
	{
		STREAM.println("++++ " + text);
	}

	public void h5(String text)
	{
		STREAM.println("+++++ " + text);
	}

	public void line(HeadingLevel headingLevel, String text)
	{
		STREAM.println(text + " _");
	}

	public void paragraph(HeadingLevel headingLevel, String text)
	{
		STREAM.println(text);
		STREAM.println();
	}

	public String strongBegin()
	{
		return "**";
	}

	public String strongEnd()
	{
		return "**";
	}

	public void listBegin(HeadingLevel headingLevel)
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

	public void codeBegin(HeadingLevel headingLevel)
	{
		STREAM.println("[[code]]");
	}
	
	public void codeLine(String code)
	{
		STREAM.println(code);
	}
	
	public void codeEnd()
	{
		STREAM.println("[[/code]]");
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
