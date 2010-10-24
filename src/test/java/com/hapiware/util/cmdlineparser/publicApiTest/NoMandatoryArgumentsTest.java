package com.hapiware.util.cmdlineparser.publicApiTest;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hapiware.util.cmdlineparser.AnnotatedFieldSetException;
import com.hapiware.util.cmdlineparser.Argument;
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.CommandNotFoundException;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.IllegalCommandLineArgumentException;
import com.hapiware.util.cmdlineparser.Option;
import com.hapiware.util.cmdlineparser.OptionArgument;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;

public class NoMandatoryArgumentsTest
	extends
		TestBase
{
	private CommandLineParser _parser;

	@BeforeMethod
	public void init()
	{
		_parser =
			new CommandLineParser(
				NoMandatoryArgumentsTest.class,
				new Description().description("Main description.")
			);
		_parser.add(Integer.class, new Argument<Integer>("PID") {{
			optional(-100);
			description("Description for PID.");
		}});
		_parser.add(Integer.class, new Argument<Integer>("TYPE") {{
			optional(-200);
			description("Description for TYPE.");
		}});
		_parser.add(Integer.class, new Argument<Integer>("ACTION") {{
			optional(-300);
			description("Description for ACTION.");
		}});
		_parser.add(new Option("a") {{
			description("Description for -a.");
			set(Integer.class, new OptionArgument<Integer>() {{
				optional(-11);
			}});
		}});
		_parser.add(new Option("b") {{
			description("Description for -b.");
			set(Integer.class, new OptionArgument<Integer>());
		}});
		_parser.add(new Option("c") {{
			description("Description for -c.");
		}});
	}

	@Test
	public void allOptionsAndArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-a11", "-b22", "-c", "100", "200", "300" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
		
		_parser.parse(
			new String[] { "-a", "11", "-b", "22", "-c", "100", "200", "300" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
	}
	
	@Test
	public void noOptionsAndAllArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "100", "200", "300" }
		);
		assertEquals(_parser.optionExists("-a"), false);
		assertEquals(_parser.getOptionValue("-a"), null);
		assertEquals(_parser.optionExists("-b"), false);
		assertEquals(_parser.getOptionValue("-b"), null);
		assertEquals(_parser.optionExists("-c"), false);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
	}

	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Too many command line arguments\\. Expected max: 3 but was: 5\\."
	)
	public void noOptionsAndTooManyArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "100", "200", "300", "400", "500" }
		);
	}
	
	@Test
	public void noArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] {  }
		);
		assertEquals(_parser.optionExists("-a"), false);
		assertEquals(_parser.getOptionValue("-a"), null);
		assertEquals(_parser.optionExists("-b"), false);
		assertEquals(_parser.getOptionValue("-b"), null);
		assertEquals(_parser.optionExists("-c"), false);
		assertEquals(_parser.getArgumentValue("PID"), -100);
		assertEquals(_parser.getArgumentValue("TYPE"), -200);
		assertEquals(_parser.getArgumentValue("ACTION"), -300);
	}
	
	@Test
	public void noArgumentsAllOptions()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-a", "11", "-b", "22", "-c" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), -100);
		assertEquals(_parser.getArgumentValue("TYPE"), -200);
		assertEquals(_parser.getArgumentValue("ACTION"), -300);
	}
	
	@Test
	public void noOptionsAndOneOptionalArgument()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "100" }
		);
		assertEquals(_parser.optionExists("-a"), false);
		assertEquals(_parser.getOptionValue("-a"), null);
		assertEquals(_parser.optionExists("-b"), false);
		assertEquals(_parser.getOptionValue("-b"), null);
		assertEquals(_parser.optionExists("-c"), false);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), -200);
		assertEquals(_parser.getArgumentValue("ACTION"), -300);
	}
	
	@Test
	public void noOptionsAndTwoOptionalArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "100", "200" }
		);
		assertEquals(_parser.optionExists("-a"), false);
		assertEquals(_parser.getOptionValue("-a"), null);
		assertEquals(_parser.optionExists("-b"), false);
		assertEquals(_parser.getOptionValue("-b"), null);
		assertEquals(_parser.optionExists("-c"), false);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), -300);
	}
	
	@Test
	public void allOptionsAWithDefaultArgumentAndAllArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-a", "-b22", "-c", "100", "200", "300" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), -11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
	}
	
	@Test
	public void allOptionsAWithDefaultArgumentAndTwoArgumentsOnly()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-a", "-b22", "-c", "100", "200" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), -11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), -300);
	}
	
	@Test
	public void optionAOnlyAndAllArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-a11", "100", "200", "300" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), false);
		assertEquals(_parser.getOptionValue("-b"), null);
		assertEquals(_parser.optionExists("-c"), false);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
	}
	
	@Test
	public void optionAOnlyAndTwoArgumentsOnly()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-a11", "100", "200" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), false);
		assertEquals(_parser.getOptionValue("-b"), null);
		assertEquals(_parser.optionExists("-c"), false);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), -300);
	}
	
	@Test
	public void optionAOnlyAndOneArgumentOnly()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-a", "11", "100" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), false);
		assertEquals(_parser.getOptionValue("-b"), null);
		assertEquals(_parser.optionExists("-c"), false);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), -200);
		assertEquals(_parser.getArgumentValue("ACTION"), -300);
	}
	
	@Test
	public void optionAWithSeeminglyDefaultArgumentAndAllArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-a", "100", "200", "300" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 100);
		assertEquals(_parser.optionExists("-b"), false);
		assertEquals(_parser.getOptionValue("-b"), null);
		assertEquals(_parser.optionExists("-c"), false);
		assertEquals(_parser.getArgumentValue("PID"), 200);
		assertEquals(_parser.getArgumentValue("TYPE"), 300);
		assertEquals(_parser.getArgumentValue("ACTION"), -300);
	}
	
	@Test
	public void someOptionsAfterArgumentsAndAllArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-b", "22", "-c", "100", "200", "300", "-a", "11" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
	}
	
	@Test
	public void someOptionsAfterArgumentsAndAllArguments2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-b", "22", "100", "200", "300", "-a", "11", "-c" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
	}
	
	@Test
	public void someOptionsAfterArgumentsAndAllArguments2AWithDefaultValue()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-b", "22", "100", "200", "300", "-a", "-c" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), -11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
	}
	
	@Test
	public void someOptionsAfterArgumentsAndAllArguments3()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-c", "100", "200", "300", "-a", "11", "-b", "22" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
	}
	
	@Test
	public void someOptionsAfterArgumentsAndTwoArgumentsOnlyAWithDefaultValue()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-b", "22", "100", "200", "-a", "-c" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), -11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), -300);
	}
	
	@Test
	public void someOptionsAfterArgumentsAndOneArgumentAndAWithDefaultValue()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-b", "22", "100", "-a", "-c" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), -11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), -200);
		assertEquals(_parser.getArgumentValue("ACTION"), -300);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Command line argument '400' cannot be interpreted as a proper "
				+ "command line argument\\. All the arguments must be sequentially positioned\\. "
				+ "Check that there are no options between arguments\\."
	)
	public void optionBBetweenArgumentsAndMandatoryArgumentsOnly()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "100", "200", "300", "-b22", "400" }
		);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Command line argument '300' cannot be interpreted as a proper "
				+ "command line argument\\. All the arguments must be sequentially positioned\\. "
				+ "Check that there are no options between arguments\\."
	)
	public void optionBBetweenArgumentsAndMandatoryArgumentsOnly2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "100", "200", "-b22", "300", "400" }
		);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Command line argument '200' cannot be interpreted as a proper "
				+ "command line argument\\. All the arguments must be sequentially positioned\\. "
				+ "Check that there are no options between arguments\\."
	)
	public void optionBBetweenArgumentsAndMandatoryArgumentsOnly3()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "100", "-b22", "200", "300" }
		);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Command line argument '200' cannot be interpreted as a proper "
				+ "command line argument\\. All the arguments must be sequentially positioned\\. "
				+ "Check that there are no options between arguments\\."
	)
	public void optionCBetweenArgumentsAndMandatoryArgumentsOnly()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "100", "-c", "200", "300" }
		);
	}
	
}

