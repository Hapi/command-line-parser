package com.hapiware.util.cmdlineparser.writer;

import java.io.PrintStream;

public class GitHubWriter
	implements
		Writer
{
	private final PrintStream _printStream;

	
	private GitHubWriter(PrintStream printStream)
	{
		_printStream = printStream;
	}
	
	@SuppressWarnings("unused")
	private static GitHubWriter createForTesting(PrintStream stream)
	{
		return new GitHubWriter(stream);
	}
	
	public GitHubWriter()
	{
		_printStream = System.out;
	}
	
	public void header()
	{
		// Does nothing.
	}

	public void level1Begin(String text)
	{
		_printStream.println("# " + text);
	}

	public void level1End()
	{
		// Does nothing.
	}

	public void level2Begin(String text)
	{
		_printStream.println("## " + text);
	}

	public void level2End()
	{
		// Does nothing.
	}

	public void level3Begin(String text)
	{
		_printStream.println("### " + text);
	}

	public void level3End()
	{
		// Does nothing.
	}

	public void level4Begin(String text)
	{
		_printStream.println("#### " + text);
	}

	public void level4End()
	{
		// Does nothing.
	}

	public void level5Begin(String text)
	{
		_printStream.println("##### " + text);
	}

	public void level5End()
	{
		// Does nothing.
	}

	public void paragraph(Level headingLevel, String text)
	{
		_printStream.println(text);
		_printStream.println();
	}

	public void line(Level headingLevel, String text)
	{
		_printStream.println(text);
	}

	public void listBegin(Level headingLevel)
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

	public void codeBegin(Level headingLevel)
	{
		// Does nothing.
	}

	public void codeLine(String code)
	{
		_printStream.println("\t" + code);
	}

	public void codeEnd()
	{
		_printStream.println();
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
