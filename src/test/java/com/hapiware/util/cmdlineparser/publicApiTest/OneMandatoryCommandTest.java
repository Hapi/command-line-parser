package com.hapiware.util.cmdlineparser.publicApiTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hapiware.util.cmdlineparser.AnnotatedFieldSetException;
import com.hapiware.util.cmdlineparser.Argument;
import com.hapiware.util.cmdlineparser.Command;
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.CommandNotFoundException;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.IllegalCommandLineArgumentException;
import com.hapiware.util.cmdlineparser.Option;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;

public class OneMandatoryCommandTest
	extends
		TestBase
{
	private CommandLineParser _parser;
	
	
	@BeforeMethod
	public void init()
	{
		_parser =
			new CommandLineParser(
				OneMandatoryCommandTest.class,
				new Description().description("Main description.")
			);
		_parser.add(new Command("set", "set description") {{
			description("Description");
			add(String.class, new Argument<String>("LEVEL") {{
				description("LEVEL description.");
			}});
			add(new Option("a") {{
				description("Description");
			}});
		}});
	}
	
	@Test
	public void fullCommandLineAllOptions()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "level", "-a" }
		);
		assertEquals(_parser.getCommand().getName(), "set");
		assertEquals(_parser.getCommand().getArgument("LEVEL").getValue(), "level");
		assertTrue((Boolean)_parser.getCommand().optionExists("-a"));
	}
	
	@Test
	public void fullCommandLineAllOptions2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "level" }
		);
		assertEquals(_parser.getCommand().getName(), "set");
		assertEquals(_parser.getCommand().getArgument("LEVEL").getValue(), "level");
		assertTrue((Boolean)_parser.getCommand().optionExists("-a"));
	}
	
	@Test
	public void fullCommandLineNoOptions()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "level"}
		);
		assertEquals(_parser.getCommand().getName(), "set");
		assertEquals(_parser.getCommand().getArgument("LEVEL").getValue(), "level");
		assertFalse((Boolean)_parser.getCommand().optionExists("-a"));
	}
	
	@Test(
		expectedExceptions = {IllegalCommandLineArgumentException.class},
		expectedExceptionsMessageRegExp = "Command 'set' does not have a mandatory argument\\."
	)
	public void noCommandNorOptions()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set" }
		);
	}
	
	@Test(
		expectedExceptions = {IllegalCommandLineArgumentException.class},
		expectedExceptionsMessageRegExp = "Command 'set' does not have a mandatory argument\\."
	)
	public void noCommandAndOneOption()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a" }
		);
	}
}
