package com.hapiware.util.cmdlineparser;

import static junit.framework.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hapiware.util.cmdlineparser.AnnotatedFieldSetException;
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.CommandNotFoundException;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.IllegalCommandLineArgumentException;
import com.hapiware.util.cmdlineparser.Option;
import com.hapiware.util.cmdlineparser.OptionArgument;
import com.hapiware.util.cmdlineparser.annotation.Id;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;
import com.hapiware.util.cmdlineparser.constraint.Enumeration;


public class GlobalOptionTest
	extends
		TestBase
{
	@Id("ver")
	private boolean _ver;
	
	@Id("n")
	private int _n;
	
	@Id("s")
	private String _s;
	
	@Id("d")
	private String _d;
	
	@Id("intarray")
	private int[] _nums;
	
	@Test
	public void normalCase()
	{
		CommandLineParser p =
			new CommandLineParser(
				ParserTest.class,
				new Description().description("Main description.")
			);
		p.add(new Option("v") {{
			alternatives("verbose").id("ver");
			description("Description");
		}});
		p.add(new Option("n") {{
			alternatives("number");
			description("Description");
			set(Integer.class, new OptionArgument<Integer>() {{
				minValue(1);
				maxValue(1000);
			}});
		}});
		p.add(new Option("s") {{
			description("Description");
			set(String.class, new OptionArgument<String>() {{
				maxLength(5);
			}});
		}});
		p.add(new Option("d") {{
			description("Description");
			set(String.class, new OptionArgument<String>() {{
				constraint(new Enumeration<String>() {{
					valueIgnoreCase("J", "Desc J.");
					value("x", "Desc x.");
					value("4", "Desc 4.");
				}});
			}});
		}});
		
		try {
			p.parse(this, new String[] { "-sSpeed", "--verbose", "--number", "1000", "-d", "j" });
			assertEquals(true, p.optionExists("-s"));
			assertEquals(false, p.optionExists("-a"));
			assertEquals(true, p.optionExists("-v"));
			assertEquals(true, p.optionExists("--verbose"));
			assertEquals(true, p.optionExists("-n"));
		
			assertEquals(1000, p.optionValue("-n"));
			assertEquals(1000, p.optionValue("--number"));
			assertEquals(null, p.optionValue("-v"));
			assertEquals("Speed", p.optionValue("-s"));
			assertEquals("j", p.optionValue("-d"));
			
			assertEquals(true, _ver);
			assertEquals(1000, _n);
			assertEquals("Speed", _s);
			assertEquals("j", _d);
		}
		catch(ConstraintException e) {
			Assert.fail("Unexpected constraint exception thrown. " + e.getMessage(), e);
		}
		catch(AnnotatedFieldSetException e) {
			Assert.fail("Unexpected annotation related exception thrown. " + e.getMessage(), e);
		}
		catch(CommandNotFoundException e) {
			Assert.fail("Unexpected command related exception thrown. " + e.getMessage(), e);
		}
		catch(IllegalCommandLineArgumentException e) {
			Assert.fail("Unexpected command line argument exception thrown. " + e.getMessage(), e);
		}
	}
	
	@Test
	public void defaultValuesForOptionalArguments()
	{
		CommandLineParser p =
			new CommandLineParser(
				ParserTest.class,
				new Description().description("Main description.")
			);
		p.add(new Option("n") {{
			description("Description");
			set(Integer.class, new OptionArgument<Integer>() {{
				optional(13);
				minValue(1);
				maxValue(1000);
			}});
		}});
		p.add(new Option("d") {{
			description("Description");
		}});
		
		try {
			p.parse(this, new String[] { "-d" });
			assertEquals(0, _n);
			p.parse(this, new String[] { "-n" });
			assertEquals(13, _n);
			p.parse(this, new String[] { "-n", "-d" });
			assertEquals(13, _n);
			p.parse(this, new String[] { "-n", "1000" });
			assertEquals(1000, _n);
			p.parse(this, new String[] { "-d", "-n" });
			assertEquals(13, _n);
			p.parse(this, new String[] { "-d", "-n", "1000" });
			assertEquals(1000, _n);
			p.parse(this, new String[] { "-n", "1000", "-d" });
			assertEquals(1000, _n);
		}
		catch(ConstraintException e) {
			Assert.fail("Unexpected constraint exception thrown. " + e.getMessage(), e);
		}
		catch(AnnotatedFieldSetException e) {
			Assert.fail("Unexpected annotation related exception thrown. " + e.getMessage(), e);
		}
		catch(CommandNotFoundException e) {
			Assert.fail("Unexpected command related exception thrown. " + e.getMessage(), e);
		}
		catch(IllegalCommandLineArgumentException e) {
			Assert.fail("Unexpected command line argument exception thrown. " + e.getMessage(), e);
		}
	}
	
	@Test
	public void settingAnnotatedArray()
	{
		CommandLineParser p =
			new CommandLineParser(
				ParserTest.class,
				new Description().description("Main description.")
			);
		p.add(new Option("nums") {{
			multiple().id("intarray");
			description("Description");
			set(Integer.class, new OptionArgument<Integer>() {{
				minValue(1);
				maxValue(10);
			}});
		}});
		
		try {
			p.parse(this, new String[] { "--nums", "1", "--nums", "3", "--nums", "9", });
			assertEquals(3, _nums.length);
			assertEquals(1, _nums[0]);
			assertEquals(3, _nums[1]);
			assertEquals(9, _nums[2]);
		}
		catch(ConstraintException e) {
			Assert.fail("Unexpected constraint exception thrown. " + e.getMessage(), e);
		}
		catch(AnnotatedFieldSetException e) {
			Assert.fail("Unexpected annotation related exception thrown. " + e.getMessage(), e);
		}
		catch(CommandNotFoundException e) {
			Assert.fail("Unexpected command related exception thrown. " + e.getMessage(), e);
		}
		catch(IllegalCommandLineArgumentException e) {
			Assert.fail("Unexpected command line argument exception thrown. " + e.getMessage(), e);
		}
	}
}
