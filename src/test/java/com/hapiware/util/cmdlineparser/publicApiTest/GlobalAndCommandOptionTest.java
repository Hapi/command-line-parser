package com.hapiware.util.cmdlineparser.publicApiTest;

import static org.testng.Assert.assertEquals;

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
import com.hapiware.util.cmdlineparser.OptionArgument;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;

public class GlobalAndCommandOptionTest
	extends
		TestBase
{
	private CommandLineParser _parser;

	@BeforeMethod
	public void init()
	{
		_parser =
			new CommandLineParser(
				TwoOptionalCommandArgumentsTest.class,
				new Description().description("Main description.")
			);
		_parser.add(new Option("x") {{
			set(Integer.class, new OptionArgument<Integer>());
			description("description");
		}});
		_parser.add(new Option("y") {{
			set(Integer.class, new OptionArgument<Integer>());
			description("description");
		}});
		_parser.add(new Option("c") {{
			set(Integer.class, new OptionArgument<Integer>().optional(-3000));
			description("description");
		}});
		
		_parser.add(new Command("set", "Short desc for set") {{
			description("description");
			add(Integer.class, new Argument<Integer>("PID") {{
				description("Description for PID.");
			}});
			add(Integer.class, new Argument<Integer>("TYPE") {{
				description("Description for TYPE.");
			}});
			add(Integer.class, new Argument<Integer>("ACTION") {{
				description("Description for ACTION.");
			}});
			add(Integer.class, new Argument<Integer>("LEVEL") {{
				description("Description for LEVEL.");
			}});
			add(new Option("a") {{
				description("Description for -a.");
				set(Integer.class, new OptionArgument<Integer>() {{
					optional(-11);
				}});
			}});
			add(new Option("b") {{
				description("Description for -b.");
				set(Integer.class, new OptionArgument<Integer>());
			}});
			add(new Option("c") {{
				description("Description for -c.");
			}});
		}});
	}

	@Test
	public void allOptionsAndArgumentsNormallyOrganised()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] {
				"-x1000", "-y2000", "-c3000",
				"set", "-a11", "-b22", "-c", "100", "200", "300", "400"
			}
		);
		
		assertEquals(_parser.getOptionValue("-x"), 1000);
		assertEquals(_parser.getOptionValue("-y"), 2000);
		assertEquals(_parser.getOptionValue("-c"), 3000);
		Command.Data command = _parser.getCommand();
		assertEquals(command.getName(), "set");
		assertEquals(command.optionExists("-a"), true);
		assertEquals(command.getOptionValue("-a"), 11);
		assertEquals(command.optionExists("-b"), true);
		assertEquals(command.getOptionValue("-b"), 22);
		assertEquals(command.optionExists("-c"), true);
		assertEquals(command.getArgumentValue("PID"), 100);
		assertEquals(command.getArgumentValue("TYPE"), 200);
		assertEquals(command.getArgumentValue("ACTION"), 300);
		assertEquals(command.getArgumentValue("LEVEL"), 400);
		
		_parser.parse(
			new String[] {
				"-x", "1000", "-y", "2000", "-c", "3000",
				"set", "-a", "11", "-b", "22", "-c", "100", "200", "300", "400"
			}
		);
		assertEquals(_parser.getOptionValue("-x"), 1000);
		assertEquals(_parser.getOptionValue("-y"), 2000);
		assertEquals(_parser.getOptionValue("-c"), 3000);
		command = _parser.getCommand();
		assertEquals(command.getName(), "set");
		assertEquals(command.optionExists("-a"), true);
		assertEquals(command.getOptionValue("-a"), 11);
		assertEquals(command.optionExists("-b"), true);
		assertEquals(command.getOptionValue("-b"), 22);
		assertEquals(command.optionExists("-c"), true);
		assertEquals(command.getArgumentValue("PID"), 100);
		assertEquals(command.getArgumentValue("TYPE"), 200);
		assertEquals(command.getArgumentValue("ACTION"), 300);
		assertEquals(command.getArgumentValue("LEVEL"), 400);
	}
	
	@Test
	public void allOptionsAndArgumentsGlobalOptionsAfterArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] {
				"set", "-a", "11", "-b", "22", "-c", "100", "200", "300", "400",
				"-x", "1000", "-y", "2000", "-c", "3000"
			}
		);
		
		assertEquals(_parser.getOptionValue("-x"), 1000);
		assertEquals(_parser.getOptionValue("-y"), 2000);
		assertEquals(_parser.getOptionValue("-c"), 3000);
		Command.Data command = _parser.getCommand();
		assertEquals(command.getName(), "set");
		assertEquals(command.optionExists("-a"), true);
		assertEquals(command.getOptionValue("-a"), 11);
		assertEquals(command.optionExists("-b"), true);
		assertEquals(command.getOptionValue("-b"), 22);
		assertEquals(command.optionExists("-c"), true);
		assertEquals(command.getArgumentValue("PID"), 100);
		assertEquals(command.getArgumentValue("TYPE"), 200);
		assertEquals(command.getArgumentValue("ACTION"), 300);
		assertEquals(command.getArgumentValue("LEVEL"), 400);
	}
	
	@Test
	public void allOptionsAndArgumentsAllOptionsAfterArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] {
				"set", "100", "200", "300", "400",
				"-a", "11", "-b", "22", "-c",
				"-x", "1000", "-y", "2000", "-c", "3000"
			}
		);
		
		assertEquals(_parser.getOptionValue("-x"), 1000);
		assertEquals(_parser.getOptionValue("-y"), 2000);
		assertEquals(_parser.getOptionValue("-c"), 3000);
		Command.Data command = _parser.getCommand();
		assertEquals(command.getName(), "set");
		assertEquals(command.optionExists("-a"), true);
		assertEquals(command.getOptionValue("-a"), 11);
		assertEquals(command.optionExists("-b"), true);
		assertEquals(command.getOptionValue("-b"), 22);
		assertEquals(command.optionExists("-c"), true);
		assertEquals(command.getArgumentValue("PID"), 100);
		assertEquals(command.getArgumentValue("TYPE"), 200);
		assertEquals(command.getArgumentValue("ACTION"), 300);
		assertEquals(command.getArgumentValue("LEVEL"), 400);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"'-a' is not a valid option\\."
	)
	public void allOptionsAndArgumentsAllOptionsAfterArgumentsGlobalOptionsBeforeCommandOptions()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] {
				"set", "100", "200", "300", "400",
				"-x", "1000", "-y", "2000", "-c", "3000",
				"-a", "11", "-b", "22", "-c"
			}
		);
	}

	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Command line argument '3000' for command 'set' cannot be interpreted as a proper "
				+ "command line argument\\. All the arguments must be sequentially positioned\\. "
				+ "Check that there are no options between arguments\\."
	)
	public void allArgumentsAndTwoCommandOptionsAnd_c_AfterArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] {
				"set", "100", "200", "300", "400",
				"-a", "11", "-b", "22", "-c", "3000"
			}
		);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Option '-c' can occur only once\\."
	)
	public void allArgumentsAndTwoCommandOptionsAnd_c_c_AfterArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] {
				"set", "100", "200", "300", "400",
				"-a", "11", "-b", "22", "-c", "-c", "3000"
			}
		);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Option '-c' can occur only once\\."
	)
	public void allArgumentsAndTwoCommandOptionsAnd_c_c_AfterArguments2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] {
				"set", "100", "200", "300", "400",
				"-a", "11", "-c", "-b", "22", "-c", "3000"
			}
		);
	}
	
	@Test
	public void c_AndArgumentsAndAllCommandOptionsAfterArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] {
				"-c", "3000",
				"set", "100", "200", "300", "400",
				"-a", "11", "-b", "22", "-c"
			}
		);
		
		assertEquals(_parser.optionExists("-x"), false);
		assertEquals(_parser.optionExists("-y"), false);
		assertEquals(_parser.getOptionValue("-c"), 3000);
		Command.Data command = _parser.getCommand();
		assertEquals(command.getName(), "set");
		assertEquals(command.optionExists("-a"), true);
		assertEquals(command.getOptionValue("-a"), 11);
		assertEquals(command.optionExists("-b"), true);
		assertEquals(command.getOptionValue("-b"), 22);
		assertEquals(command.optionExists("-c"), true);
		assertEquals(command.getArgumentValue("PID"), 100);
		assertEquals(command.getArgumentValue("TYPE"), 200);
		assertEquals(command.getArgumentValue("ACTION"), 300);
		assertEquals(command.getArgumentValue("LEVEL"), 400);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Option '-c' can occur only once\\."
	)
	public void allCommandOptionsAndArgumentsAnd_c_AfterCommandArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] {
				"set", "-a", "11", "-b", "22", "-c", "100", "200", "300", "400",
				 "-c", "3000"
			}
		);
	}
	
	public void allCommandOptionsAndArgumentsAnd_x_c_AfterCommandArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] {
				"set", "-a", "11", "-b", "22", "-c", "100", "200", "300", "400",
				 "x", "1000", "-c"
			}
		);
		assertEquals(_parser.getOptionValue("-x"), 1000);
		assertEquals(_parser.optionExists("-y"), false);
		assertEquals(_parser.getOptionValue("-c"), -3000);
		Command.Data command = _parser.getCommand();
		assertEquals(command.getName(), "set");
		assertEquals(command.optionExists("-a"), true);
		assertEquals(command.getOptionValue("-a"), 11);
		assertEquals(command.optionExists("-b"), true);
		assertEquals(command.getOptionValue("-b"), 22);
		assertEquals(command.optionExists("-c"), true);
		assertEquals(command.getArgumentValue("PID"), 100);
		assertEquals(command.getArgumentValue("TYPE"), 200);
		assertEquals(command.getArgumentValue("ACTION"), 300);
		assertEquals(command.getArgumentValue("LEVEL"), 400);
	}

	public void twoCommandOptionsAndArgumentsAnd_c_x_c_AfterCommandArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] {
				"set", "-a", "11", "-b", "22", "100", "200", "300", "400",
				 "-c", "x", "1000", "-c",
			}
		);
		assertEquals(_parser.getOptionValue("-x"), 1000);
		assertEquals(_parser.optionExists("-y"), false);
		assertEquals(_parser.getOptionValue("-c"), -3000);
		Command.Data command = _parser.getCommand();
		assertEquals(command.getName(), "set");
		assertEquals(command.optionExists("-a"), true);
		assertEquals(command.getOptionValue("-a"), 11);
		assertEquals(command.optionExists("-b"), true);
		assertEquals(command.getOptionValue("-b"), 22);
		assertEquals(command.optionExists("-c"), true);
		assertEquals(command.getArgumentValue("PID"), 100);
		assertEquals(command.getArgumentValue("TYPE"), 200);
		assertEquals(command.getArgumentValue("ACTION"), 300);
		assertEquals(command.getArgumentValue("LEVEL"), 400);
	}
}
