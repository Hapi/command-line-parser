package com.hapiware.util.cmdlineparser.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;


/**
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class HtmlWriter
	implements
		Writer
{
	private final static Logger LOGGER = Logger.getLogger(HtmlWriter.class.getName());
	
	private final static int TAB_SIZE = 4;
	private final static String DEFAULT_ENCODING = "UTF-8";
	
	private final OutputStream _outputStream;
	private final String _encoding;
	
	
	public HtmlWriter()
	{
		this(DEFAULT_ENCODING, System.out);
	}
	
	public HtmlWriter(OutputStream os)
	{
		this(DEFAULT_ENCODING, os);
	}

	public HtmlWriter(String encoding, OutputStream os)
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
		println(tab(1) + "<h1>" + text + "</h1>");
	}

	public void level1End()
	{
		println("");
	}

	public void level2Begin(String text)
	{
		println(tab(1) + "<h2>" + text + "</h2>");
	}

	public void level2End()
	{
		// Does nothing.
	}

	public void level3Begin(String text)
	{
		println(tab(1) + "<h3>" + text + "</h3>");
	}

	public void level3End()
	{
		// Does nothing.
	}

	public void level4Begin(String text)
	{
		println(tab(1) + "<h4>" + text + "</h4>");
	}

	public void level4End()
	{
		// Does nothing.
	}

	public void level5Begin(String text)
	{
		println(tab(1) + "<h5>" + text + "</h5>");
	}

	public void level5End()
	{
		// Does nothing.
	}

	public void line(Level level, String text)
	{
		println(tab(1) + text);
	}

	public void paragraph(Level level, String text)
	{
		println(tab(1) + "<p>" + text + "</p>");
	}

	public String strongBegin()
	{
		return "<b>";
	}

	public String strongEnd()
	{
		return "</b>";
	}

	public void listBegin(Level level)
	{
		println(tab(1) + "<ul>");
	}

	public void listItem(String text)
	{
		println(tab(2) + "<li>" + text + "</li>");
	}

	public void listEnd()
	{
		println(tab(1) + "</ul>");
	}

	public void codeBegin(Level level)
	{
		println(tab(1) + "<div class=\"code\">");
	}
	
	public void codeLine(String code)
	{
		println(tab(2) + "<p>" + code + "</p>");
	}
	
	public void codeEnd()
	{
		println(tab(1) + "</div>");
	}
	
	public void header()
	{
		println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">");
		println("<html>");
		println("<head />");
		println("<body>");
	}
	
	public void footer()
	{
		println("</body>");
		println("</html>");
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
}
