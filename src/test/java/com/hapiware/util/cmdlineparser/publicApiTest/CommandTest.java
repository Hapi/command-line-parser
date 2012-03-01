package com.hapiware.util.cmdlineparser.publicApiTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hapiware.util.cmdlineparser.AnnotatedFieldSetException;
import com.hapiware.util.cmdlineparser.Argument;
import com.hapiware.util.cmdlineparser.Command;
import com.hapiware.util.cmdlineparser.Command.Data;
import com.hapiware.util.cmdlineparser.CommandExecutor;
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.CommandNotFoundException;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.IllegalCommandLineArgumentException;
import com.hapiware.util.cmdlineparser.Option;
import com.hapiware.util.cmdlineparser.OptionArgument;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;
import com.hapiware.util.cmdlineparser.constraint.Enumeration;


public class CommandTest
	extends
		TestBase
{
	private CommandLineParser _parser;
	
	
	@BeforeMethod
	public void init()
	{
		_parser =
			new CommandLineParser(
				CommandTest.class,
				new Description().description("Main description.")
			);
		_parser.add(new Command("set", "set description") {{
			description("Description");
			add(Integer.class, new Argument<Integer>("PID") {{
				description("PID description");
				minValue(-2);
				maxValue(150);
			}});
			add(Integer.class, new Argument<Integer>("TYPE") {{
				description("TYPE description.");
				optional(4);
				constraint(new Enumeration<Integer>() {{
					value(2, "2 desc.");
					value(3, "3 desc.");
					value(4, "4 desc.");
				}});
			}});
			add(String.class, new Argument<String>("LEVEL") {{
				description("LEVEL description.");
			}});
			add(new Option("a") {{
				description("Description");
				set(String.class, new OptionArgument<String>() {{
					minLength(3);
					maxLength(6);
				}});
			}});
			add(new Option("b") {{
				description("Description");
				set(Integer.class, new OptionArgument<Integer>() {{
					minValue(-3);
					maxValue(6);
				}});
			}});
		}});
	}

	@Test()
	public void fullCommandLineNoOptions()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "123", "2", "level" }
		);
		assertEquals(_parser.getCommand().getName(), "set");
		assertEquals(_parser.getCommand().getArgument("PID").getValue(), 123);
		assertEquals(_parser.getCommand().getArgument("TYPE").getValue(), 2);
		assertEquals(_parser.getCommand().getArgument("LEVEL").getValue(), "level");
	}

	@Test()
	public void fullCommandLineAllOptions()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abc", "-b", "-2", "123", "2", "level" }
		);
		assertEquals(_parser.getCommand().getName(), "set");
		assertEquals(_parser.getCommand().getArgument("PID").getValue(), 123);
		assertEquals(_parser.getCommand().getArgument("TYPE").getValue(), 2);
		assertEquals(_parser.getCommand().getArgument("LEVEL").getValue(), "level");
		assertEquals(_parser.getCommand().getOptionValue("-a"), "abc");
		assertEquals(_parser.getCommand().getOptionValue("-b"), -2);

		// Different option order.
		_parser.parse(
			this,
			new String[] { "set", "-b", "-2", "123", "2", "level", "-a", "abc" }
		);
		assertEquals(_parser.getCommand().getName(), "set");
		assertEquals(_parser.getCommand().getArgument("PID").getValue(), 123);
		assertEquals(_parser.getCommand().getArgument("TYPE").getValue(), 2);
		assertEquals(_parser.getCommand().getArgument("LEVEL").getValue(), "level");
		assertEquals(_parser.getCommand().getOptionValue("-a"), "abc");
		assertEquals(_parser.getCommand().getOptionValue("-b"), -2);
		_parser.parse(
			this,
			new String[] { "set", "123", "2", "level", "-b", "-2", "-a", "abc" }
		);
		assertEquals(_parser.getCommand().getName(), "set");
		assertEquals(_parser.getCommand().getArgument("PID").getValue(), 123);
		assertEquals(_parser.getCommand().getArgument("TYPE").getValue(), 2);
		assertEquals(_parser.getCommand().getArgument("LEVEL").getValue(), "level");
		assertEquals(_parser.getCommand().getOptionValue("-a"), "abc");
		assertEquals(_parser.getCommand().getOptionValue("-b"), -2);
	}
	
	@Test(
		expectedExceptions = {IllegalCommandLineArgumentException.class},
		expectedExceptionsMessageRegExp =
			"Too few command line arguments for command 'set'. Expected min: 2 but was: 1\\. "
				+ "Check that there are no options between arguments\\."
	)
	public void fullCommandLineWrongOptionPositions()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abc", "123", "-b", "-2", "2", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {IllegalCommandLineArgumentException.class},
		expectedExceptionsMessageRegExp =
			"Command line argument 'level' for command 'set' cannot be interpreted as a proper "
				+ "command line argument\\. All the arguments must be sequentially positioned\\. "
				+ "Check that there are no options between arguments\\."
	)
	public void fullCommandLineWrongOptionPositions2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abc", "123", "2", "-b", "-2", "level" }
		);
	}
	
	@Test()
	public void commandLineOptionalArgumentMissingNoOptions()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "123", "level" }
		);
		assertEquals(_parser.getCommand().getName(), "set");
		assertEquals(_parser.getCommand().getArgument("PID").getValue(), 123);
		assertEquals(_parser.getCommand().getArgument("TYPE").getValue(), 4);
		assertEquals(_parser.getCommand().getArgument("LEVEL").getValue(), "level");
	}

	
	@Test()
	public void commandLineOptionalArgumentMissingAllOptions()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abc", "-b", "-2", "123", "level" }
		);
		assertEquals(_parser.getCommand().getName(), "set");
		assertEquals(_parser.getCommand().getArgument("PID").getValue(), 123);
		assertEquals(_parser.getCommand().getArgument("TYPE").getValue(), 4);
		assertEquals(_parser.getCommand().getArgument("LEVEL").getValue(), "level");
		assertEquals(_parser.getCommand().getOptionValue("-a"), "abc");
		assertEquals(_parser.getCommand().getOptionValue("-b"), -2);

		// Different option order.
		_parser.parse(
			this,
			new String[] { "set", "-b", "-2", "123", "level", "-a", "abc" }
		);
		assertEquals(_parser.getCommand().getName(), "set");
		assertEquals(_parser.getCommand().getArgument("PID").getValue(), 123);
		assertEquals(_parser.getCommand().getArgument("TYPE").getValue(), 4);
		assertEquals(_parser.getCommand().getArgument("LEVEL").getValue(), "level");
		assertEquals(_parser.getCommand().getOptionValue("-a"), "abc");
		assertEquals(_parser.getCommand().getOptionValue("-b"), -2);
		_parser.parse(
			this,
			new String[] { "set", "123", "level", "-b", "-2", "-a", "abc" }
		);
		assertEquals(_parser.getCommand().getName(), "set");
		assertEquals(_parser.getCommand().getArgument("PID").getValue(), 123);
		assertEquals(_parser.getCommand().getArgument("TYPE").getValue(), 4);
		assertEquals(_parser.getCommand().getArgument("LEVEL").getValue(), "level");
		assertEquals(_parser.getCommand().getOptionValue("-a"), "abc");
		assertEquals(_parser.getCommand().getOptionValue("-b"), -2);
	}
	
	@Test(
		expectedExceptions = {ConstraintException.class},
		expectedExceptionsMessageRegExp =
			"Length of 'ab' is shorter than the minimum length 3 allowed for '-a'\\."
	)
	public void fullCommandLineAllOptionsOptionATooShort()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "ab", "-b", "-2", "123", "2", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {ConstraintException.class},
		expectedExceptionsMessageRegExp =
			"Length of 'abcdefg' is longer than the maximum length 6 allowed for '-a'\\."
	)
	public void fullCommandLineAllOptionsOptionATooLong()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abcdefg", "-b", "-2", "123", "2", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {ConstraintException.class},
		expectedExceptionsMessageRegExp =
			"'-4' is smaller than the minimum value -3 allowed for '-b'\\."
	)
	public void fullCommandLineAllOptionsOptionBTooSmall()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abc", "-b", "-4", "123", "2", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {ConstraintException.class},
		expectedExceptionsMessageRegExp =
			"'7' is greater than the maximum value 6 allowed for '-b'\\."
	)
	public void fullCommandLineAllOptionsOptionBTooBig()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abc", "-b", "7", "123", "2", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {ConstraintException.class},
		expectedExceptionsMessageRegExp =
			"'-3' is smaller than the minimum value -2 allowed for 'PID'\\."
	)
	public void fullCommandLineAllOptionsArgumentPidTooSmall()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abc", "-b", "-2", "-3", "2", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {ConstraintException.class},
		expectedExceptionsMessageRegExp =
			"'151' is greater than the maximum value 150 allowed for 'PID'\\."
	)
	public void fullCommandLineAllOptionsArgumentPidTooBig()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abc", "-b", "-2", "151", "2", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {IllegalCommandLineArgumentException.class},
		expectedExceptionsMessageRegExp =
			"'xyz' cannot be interpreted as java.lang.Integer for 'PID'\\."
	)
	public void fullCommandLineAllOptionsArgumentPidWrongType()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abc", "-b", "-2", "xyz", "2", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {ConstraintException.class},
		expectedExceptionsMessageRegExp =
			"Value for 'TYPE' was '1' but it must be one of these: \\[2, 3, 4\\]."
	)
	public void fullCommandLineAllOptionsArgumentTypeOutOfRange()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abc", "-b", "-2", "123", "1", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {ConstraintException.class},
		expectedExceptionsMessageRegExp =
			"Value for 'TYPE' was '6' but it must be one of these: \\[2, 3, 4\\]."
	)
	public void fullCommandLineAllOptionsArgumentTypeOutOfRange2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abc", "-b", "-2", "123", "6", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {IllegalCommandLineArgumentException.class},
		expectedExceptionsMessageRegExp =
			"'a' cannot be interpreted as java.lang.Integer for 'TYPE'\\."
	)
	public void fullCommandLineAllOptionsArgumentTypeWrongType()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "set", "-a", "abc", "-b", "-2", "123", "a", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {CommandNotFoundException.class},
		expectedExceptionsMessageRegExp =
			"A command was expected but 'non-defined-command' cannot be interpreted as a command\\."
	)
	public void nonDefinedCommand()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "non-defined-command", "123", "a", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {CommandNotFoundException.class},
		expectedExceptionsMessageRegExp =
			"A command was expected but 'non-defined-command' cannot be interpreted as a command\\."
	)
	public void nonDefinedCommandAndOptions()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "non-defined-command", "-a", "abc", "-b", "-2", "123", "a", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {CommandNotFoundException.class},
		expectedExceptionsMessageRegExp =
			"A command was expected but '123' cannot be interpreted as a command\\."
	)
	public void noCommand()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "123", "a", "level" }
		);
	}
	
	@Test(
		expectedExceptions = {IllegalCommandLineArgumentException.class},
		expectedExceptionsMessageRegExp =
			"'-a' is not a valid option\\."
	)
	public void noCommandAndOptions()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "-a", "abc", "-b", "-2", "123", "a", "level" }
		);
	}

	@Test
	public void testExecutor()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		CommandLineParser p =
			new CommandLineParser(
				CommandTest.class,
				new Description().description("Main description.")
			);
		p.add(new Option("a") {{
			description("description");
			set(String.class, new OptionArgument<String>());
		}});
		
		p.add(
			new Command(
				"run",
				"Short desc for run.",
				new CommandExecutor() {
					public void execute(
						Data command,
						List<Option.Data> globalOptions)
					{
						assertNotNull(command);
						assertEquals(globalOptions.size(), 1);
						assertEquals(globalOptions.get(0).getArgument().getValue(), "hapi");
						assertEquals(command.getArgumentValue("PID"), 123);
						assertEquals(command.getArgumentValue("TYPE"), "Java");
					}
				}
			) {{
			description("description");
			add(Integer.class, new Argument<Integer>("PID") {{
				description("PID description");
			}});
			add(String.class, new Argument<String>("TYPE") {{
				description("TYPE description");
			}});
		}});
		
		p.parse(
			this,
			new String[] { "-a", "hapi", "run", "123", "Java" }
		);
	}
}
