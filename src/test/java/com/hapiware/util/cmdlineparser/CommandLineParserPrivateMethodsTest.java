package com.hapiware.util.cmdlineparser;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import com.hapiware.util.cmdlineparser.writer.ConfluenceWriter;
import com.hapiware.util.cmdlineparser.writer.GitHubWriter;
import com.hapiware.util.cmdlineparser.writer.HtmlWriter;
import com.hapiware.util.cmdlineparser.writer.ScreenWriter;
import com.hapiware.util.cmdlineparser.writer.WikidotWriter;
import com.hapiware.util.cmdlineparser.writer.Writer;
import com.hapiware.util.cmdlineparser.writer.XmlWriter;
import com.hapiware.util.publisher.Publisher;

public class CommandLineParserPrivateMethodsTest
{
	private static final String WRITER_CLASS_PROPERTY = "writer.class";


	private interface SCommandLineParser
	{
		public Writer createSystemPropertyWriter();
	}
	
	private SCommandLineParser _sParser =
		Publisher.publish(SCommandLineParser.class, CommandLineParser.class);
	

	@AfterTest
	public void removeProperty()
	{
		System.clearProperty(WRITER_CLASS_PROPERTY);
		assertNull(_sParser.createSystemPropertyWriter());
	}
	
	
	@Test
	public void createSystemPropertyWriterShortForms()
	{
		System.setProperty(WRITER_CLASS_PROPERTY, "Screen");
		Writer writer = _sParser.createSystemPropertyWriter();
		assertNotNull(writer);
		assertEquals(writer.getClass(), ScreenWriter.class);
		
		System.setProperty(WRITER_CLASS_PROPERTY, "Html");
		writer = _sParser.createSystemPropertyWriter();
		assertNotNull(writer);
		assertEquals(writer.getClass(), HtmlWriter.class);
		
		System.setProperty(WRITER_CLASS_PROPERTY, "Xml");
		writer = _sParser.createSystemPropertyWriter();
		assertNotNull(writer);
		assertEquals(writer.getClass(), XmlWriter.class);
		
		System.setProperty(WRITER_CLASS_PROPERTY, "Wikidot");
		writer = _sParser.createSystemPropertyWriter();
		assertNotNull(writer);
		assertEquals(writer.getClass(), WikidotWriter.class);
		
		System.setProperty(WRITER_CLASS_PROPERTY, "Confluence");
		writer = _sParser.createSystemPropertyWriter();
		assertNotNull(writer);
		assertEquals(writer.getClass(), ConfluenceWriter.class);
		
		System.setProperty(WRITER_CLASS_PROPERTY, "GitHub");
		writer = _sParser.createSystemPropertyWriter();
		assertNotNull(writer);
		assertEquals(writer.getClass(), GitHubWriter.class);
	}

	@Test
	public void createSystemPropertyWriterLongForms()
	{
		System.setProperty(WRITER_CLASS_PROPERTY, "com.hapiware.util.cmdlineparser.writer.ScreenWriter");
		Writer writer = _sParser.createSystemPropertyWriter();
		assertNotNull(writer);
		assertEquals(writer.getClass(), ScreenWriter.class);
		
		System.setProperty(WRITER_CLASS_PROPERTY, "com.hapiware.util.cmdlineparser.writer.HtmlWriter");
		writer = _sParser.createSystemPropertyWriter();
		assertNotNull(writer);
		assertEquals(writer.getClass(), HtmlWriter.class);
		
		System.setProperty(WRITER_CLASS_PROPERTY, "com.hapiware.util.cmdlineparser.writer.XmlWriter");
		writer = _sParser.createSystemPropertyWriter();
		assertNotNull(writer);
		assertEquals(writer.getClass(), XmlWriter.class);
		
		System.setProperty(WRITER_CLASS_PROPERTY, "com.hapiware.util.cmdlineparser.writer.WikidotWriter");
		writer = _sParser.createSystemPropertyWriter();
		assertNotNull(writer);
		assertEquals(writer.getClass(), WikidotWriter.class);
		
		System.setProperty(WRITER_CLASS_PROPERTY, "com.hapiware.util.cmdlineparser.writer.ConfluenceWriter");
		writer = _sParser.createSystemPropertyWriter();
		assertNotNull(writer);
		assertEquals(writer.getClass(), ConfluenceWriter.class);
		
		System.setProperty(WRITER_CLASS_PROPERTY, "com.hapiware.util.cmdlineparser.writer.GitHubWriter");
		writer = _sParser.createSystemPropertyWriter();
		assertNotNull(writer);
		assertEquals(writer.getClass(), GitHubWriter.class);
	}
}
