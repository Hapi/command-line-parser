package com.hapiware.utils.cmdline.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;


/**
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class XmlWriter
	implements
		Writer
{
	private final static Logger LOGGER = Logger.getLogger(XmlWriter.class.getName());
	
	private final static int TAB_SIZE = 4;
	private final static String DEFAULT_ENCODING = "UTF-8";
	
	private final OutputStream _outputStream;
	private final String _encoding;
	private Level _levelForListItems;
	private Level _levelForCodeLines;
	
	
	public XmlWriter()
	{
		this(DEFAULT_ENCODING, System.out);
	}
	
	public XmlWriter(OutputStream os)
	{
		this(DEFAULT_ENCODING, os);
	}

	public XmlWriter(String encoding, OutputStream os)
	{
		if(encoding == null || encoding.trim().length() == 0)
			throw new NullPointerException("'encoding' must have a value.");
		if(os == null)
			throw new NullPointerException("'os' must have a value.");
		_encoding = encoding;
		_outputStream = os;
	}

	public void level1Begin(String text)
	{
		println(tab(1) + "<level-1>");
		println(tab(1 + 1) + "<heading>" + text + "</heading>");
	}

	public void level1End()
	{
		println(tab(1) + "</level-1>");
	}

	public void level2Begin(String text)
	{
		println(tab(2) + "<level-2>");
		println(tab(2 + 1) + "<heading>" + text + "</heading>");
	}

	public void level2End()
	{
		println(tab(2) + "</level-2>");
	}

	public void level3Begin(String text)
	{
		println(tab(3) + "<level-3>");
		println(tab(3 + 1) + "<heading>" + text + "</heading>");
	}

	public void level3End()
	{
		println(tab(3) + "</level-3>");
	}

	public void level4Begin(String text)
	{
		println(tab(4) + "<level-4>");
		println(tab(4 + 1) + "<heading>" + text + "</heading>");
	}

	public void level4End()
	{
		println(tab(4) + "</level-4>");
	}

	public void level5Begin(String text)
	{
		println(tab(5) + "<level-5>");
		println(tab(5 + 1) + "<heading>" + text + "</heading>");
	}

	public void level5End()
	{
		println(tab(5) + "</level-5>");
	}

	public void line(Level level, String text)
	{
		println(tab(level) + text);
	}

	public void paragraph(Level level, String text)
	{
		println(tab(level) + "<paragraph>" + text + "</paragraph>");
	}

	public String strongBegin()
	{
		return "<strong>";
	}

	public String strongEnd()
	{
		return "</strong>";
	}

	public void listBegin(Level level)
	{
		_levelForListItems = level;
		println(tab(level) + "<list>");
	}

	public void listItem(String text)
	{
		println(tab(_levelForListItems.ordinal() + 3) + "<item>" + text + "</item>");
	}

	public void listEnd()
	{
		println(tab(_levelForListItems) + "</list>");
	}

	public void codeBegin(Level level)
	{
		_levelForCodeLines = level;
		println(tab(level) + "<code>");
	}
	
	public void codeLine(String code)
	{
		println(tab(_levelForCodeLines.ordinal() + 3) + "<line>" + code + "</line>");
	}
	
	public void codeEnd()
	{
		println(tab(_levelForCodeLines) + "</code>");
	}
	
	public void header()
	{
		println("<?xml version=\"1.0\" encoding=\"" + _encoding + "\" ?>");
		println("<cmdline-out>");
	}
	
	public void footer()
	{
		println("</cmdline-out>");
	}
	
	private void println(String text)
	{
		try {
			_outputStream.write((text + "\n").getBytes(_encoding));
		}
		catch(UnsupportedEncodingException e) {
			if(LOGGER.isLoggable(java.util.logging.Level.SEVERE)) {
				LOGGER.log(
					java.util.logging.Level.SEVERE,
					_encoding + " is unsupported encoding.",
					e
				);
			}
		}
		catch(IOException e) {
			if(LOGGER.isLoggable(java.util.logging.Level.SEVERE)) {
				LOGGER.log(
					java.util.logging.Level.SEVERE,
					"[" + text + "] cannot be written to a stream.",
					e
				);
			}
		}
	}
	
	
	private static String tab(int level)
	{
		String tab = "";
		for(int i = 0; i < level * TAB_SIZE; i++)
			tab += " ";
		return tab;
	}
	private static String tab(Level level)
	{
		return tab(level.ordinal() + 2);
	}
}
