package com.hapiware.util.cmdlineparser.writer;

import java.io.PrintStream;

public class GitHubWriter
	implements
		Writer
{
	private final static PrintStream STREAM = System.out;

	public GitHubWriter()
	{
		// Does nothing.
	}
	
	public void header()
	{
		// Does nothing.
	}

	public void level1Begin(String text)
	{
		STREAM.println("# " + text);
	}

	public void level1End()
	{
		// Does nothing.
	}

	public void level2Begin(String text)
	{
		STREAM.println("## " + text);
	}

	public void level2End()
	{
		// Does nothing.
	}

	public void level3Begin(String text)
	{
		STREAM.println("### " + text);
	}

	public void level3End()
	{
		// Does nothing.
	}

	public void level4Begin(String text)
	{
		STREAM.println("#### " + text);
	}

	public void level4End()
	{
		// Does nothing.
	}

	public void level5Begin(String text)
	{
		STREAM.println("##### " + text);
	}

	public void level5End()
	{
		// Does nothing.
	}

	public void paragraph(Level headingLevel, String text)
	{
		STREAM.println(text);
		STREAM.println();
	}

	public void line(Level headingLevel, String text)
	{
		STREAM.println(text);
	}

	public void listBegin(Level headingLevel)
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

	public void codeBegin(Level headingLevel)
	{
		// Does nothing.
	}

	public void codeLine(String code)
	{
		STREAM.println("\t" + code);
	}

	public void codeEnd()
	{
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

	public void footer()
	{
		// Does nothing.
	}
}
