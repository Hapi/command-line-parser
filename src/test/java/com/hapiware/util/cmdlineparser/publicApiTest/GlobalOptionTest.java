package com.hapiware.util.cmdlineparser.publicApiTest;

import static org.testng.Assert.assertEquals;

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
				GlobalOptionTest.class,
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
			assertEquals(p.optionExists("-s"), true);
			assertEquals(p.optionExists("-a"), false);
			assertEquals(p.optionExists("-v"), true);
			assertEquals(p.optionExists("--verbose"), true);
			assertEquals(p.optionExists("-n"), true);
		
			assertEquals(p.getOptionValue("-n"), 1000);
			assertEquals(p.getOptionValue("--number"), 1000);
			assertEquals(p.getOptionValue("-v"), null);
			assertEquals(p.getOptionValue("-s"), "Speed");
			assertEquals(p.getOptionValue("-d"), "j");
			
			assertEquals(_ver, true);
			assertEquals(_n, 1000);
			assertEquals(_s, "Speed");
			assertEquals(_d, "j");
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
				GlobalOptionTest.class,
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
			assertEquals(_n, 0);
			p.parse(this, new String[] { "-n" });
			assertEquals(_n, 13);
			p.parse(this, new String[] { "-n", "-d" });
			assertEquals(_n, 13);
			p.parse(this, new String[] { "-n", "1000" });
			assertEquals(_n, 1000);
			p.parse(this, new String[] { "-d", "-n" });
			assertEquals(_n, 13);
			p.parse(this, new String[] { "-d", "-n", "1000" });
			assertEquals(_n, 1000);
			p.parse(this, new String[] { "-n", "1000", "-d" });
			assertEquals(_n, 1000);
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
				GlobalOptionTest.class,
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
			assertEquals(_nums.length, 3);
			assertEquals(_nums[0], 1);
			assertEquals(_nums[1], 3);
			assertEquals(_nums[2], 9);
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
