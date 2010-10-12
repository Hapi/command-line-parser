package com.hapiware.utils.cmdline;

import org.testng.Assert;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;

import com.hapiware.utils.cmdline.annotation.Id;
import com.hapiware.utils.cmdline.constraint.AnnotatedFieldSetException;
import com.hapiware.utils.cmdline.constraint.CommandNotFoundException;
import com.hapiware.utils.cmdline.constraint.ConstraintException;
import com.hapiware.utils.cmdline.constraint.Enumeration;
import com.hapiware.utils.cmdline.constraint.IllegalCommandLineArgumentException;
import com.hapiware.utils.cmdline.element.Argument;
import com.hapiware.utils.cmdline.element.Option;

public class SimpleTest
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
		CommandLineParser p = new CommandLineParser();
		p.add(new Option() {{
			name("v").alternatives("verbose").id("ver");
		}});
		p.add(new Option() {{
			name("n").alternatives("number");
			set(Integer.class, new Argument() {{
				minValue(1);
				maxValue(1000);
			}});
		}});
		p.add(new Option() {{
			name("s");
			set(String.class, new Argument() {{
				maxLength(5);
			}});
		}});
		p.add(new Option() {{
			name("d");
			set(String.class, new Argument() {{
				constraint(new Enumeration() {{
					value("J").ignoreCase();
					value("x");
					value("4");
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
		CommandLineParser p = new CommandLineParser();
		p.add(new Option() {{
			name("n");
			set(Integer.class, new Argument() {{
				optional(13);
				minValue(1);
				maxValue(1000);
			}});
		}});
		p.add(new Option() {{
			name("d");
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
		CommandLineParser p = new CommandLineParser();
		p.add(new Option() {{
			name("nums").multiple().id("intarray");
			set(Integer.class, new Argument() {{
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
