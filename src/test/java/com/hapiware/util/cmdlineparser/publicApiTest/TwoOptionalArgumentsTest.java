package com.hapiware.util.cmdlineparser.publicApiTest;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hapiware.util.cmdlineparser.AnnotatedFieldSetException;
import com.hapiware.util.cmdlineparser.Argument;
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.CommandNotFoundException;
import com.hapiware.util.cmdlineparser.ConfigurationException;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.IllegalCommandLineArgumentException;
import com.hapiware.util.cmdlineparser.Option;
import com.hapiware.util.cmdlineparser.OptionArgument;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;

public class TwoOptionalArgumentsTest
	extends
		TestBase
{
	private CommandLineParser _parser;

	@BeforeMethod
	public void init()
	{
		_parser =
			new CommandLineParser(
				TwoOptionalArgumentsTest.class,
				new Description().description("Main description.")
			);
		_parser.add(Integer.class, new Argument<Integer>("PID") {{
			description("Description for PID.");
		}});
		_parser.add(Integer.class, new Argument<Integer>("TYPE") {{
			description("Description for TYPE.");
		}});
		_parser.add(Integer.class, new Argument<Integer>("ACTION") {{
			optional(-300);
			description("Description for ACTION.");
		}});
		_parser.add(Integer.class, new Argument<Integer>("LEVEL") {{
			optional(-400);
			description("Description for LEVEL.");
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

	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp = 
			"If there is more than one optional argument they must be the last arguments\\. "
				+ "The first conflicting argument is 'LEVEL'\\. "
				+ "A single optional argument can have any position\\."
	)
	public void misplacedOptionalArguments()
	{
		CommandLineParser p =
			new CommandLineParser(
				TwoOptionalArgumentsTest.class,
				new Description().description("Main description.")
			);
		p.add(Integer.class, new Argument<Integer>("PID") {{
			description("Description for PID.");
		}});
		p.add(Integer.class, new Argument<Integer>("TYPE") {{
			optional(-200);
			description("Description for TYPE.");
		}});
		p.add(Integer.class, new Argument<Integer>("ACTION") {{
			optional(-300);
			description("Description for ACTION.");
		}});
		p.add(Integer.class, new Argument<Integer>("LEVEL") {{
			description("Description for LEVEL.");
		}});
	}
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp = 
			"If there is more than one optional argument they must be the last arguments\\. "
				+ "The first conflicting argument is 'LEVEL'\\. "
				+ "A single optional argument can have any position\\."
	)
	public void misplacedOptionalArguments2()
	{
		CommandLineParser p =
			new CommandLineParser(
				TwoOptionalArgumentsTest.class,
				new Description().description("Main description.")
			);
		p.add(Integer.class, new Argument<Integer>("PID") {{
			description("Description for PID.");
		}});
		p.add(Integer.class, new Argument<Integer>("TYPE") {{
			optional(-200);
			description("Description for TYPE.");
		}});
		p.add(Integer.class, new Argument<Integer>("ACTION") {{
			description("Description for ACTION.");
		}});
		p.add(Integer.class, new Argument<Integer>("LEVEL") {{
			optional(-400);
			description("Description for LEVEL.");
		}});
	}
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp = 
			"If there is more than one optional argument they must be the last arguments\\. "
				+ "The first conflicting argument is 'LEVEL'\\. "
				+ "A single optional argument can have any position\\."
	)
	public void misplacedOptionalArguments3()
	{
		CommandLineParser p =
			new CommandLineParser(
				TwoOptionalArgumentsTest.class,
				new Description().description("Main description.")
			);
		p.add(Integer.class, new Argument<Integer>("PID") {{
			description("Description for PID.");
		}});
		p.add(Integer.class, new Argument<Integer>("TYPE") {{
			optional(-200);
			description("Description for TYPE.");
		}});
		p.add(Integer.class, new Argument<Integer>("ACTION") {{
			description("Description for ACTION.");
		}});
		p.add(Integer.class, new Argument<Integer>("LEVEL") {{
			optional(-400);
			description("Description for LEVEL.");
		}});
		p.add(Integer.class, new Argument<Integer>("INFO") {{
			optional(-500);
			description("Description for INFO.");
		}});
	}
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp = 
			"If there is more than one optional argument they must be the last arguments\\. "
				+ "The first conflicting argument is 'INFO'\\. "
				+ "A single optional argument can have any position\\."
	)
	public void misplacedOptionalArguments4()
	{
		CommandLineParser p =
			new CommandLineParser(
				TwoOptionalArgumentsTest.class,
				new Description().description("Main description.")
			);
		p.add(Integer.class, new Argument<Integer>("PID") {{
			description("Description for PID.");
		}});
		p.add(Integer.class, new Argument<Integer>("TYPE") {{
			optional(-200);
			description("Description for TYPE.");
		}});
		p.add(Integer.class, new Argument<Integer>("ACTION") {{
			optional(-300);
			description("Description for ACTION.");
		}});
		p.add(Integer.class, new Argument<Integer>("LEVEL") {{
			optional(-400);
			description("Description for LEVEL.");
		}});
		p.add(Integer.class, new Argument<Integer>("INFO") {{
			description("Description for INFO.");
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
			new String[] { "-a11", "-b22", "-c", "100", "200", "300", "400" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
		assertEquals(_parser.getArgumentValue("LEVEL"), 400);
		
		_parser.parse(
			new String[] { "-a", "11", "-b", "22", "-c", "100", "200", "300", "400" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
		assertEquals(_parser.getArgumentValue("LEVEL"), 400);
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
			new String[] { "100", "200", "300", "400" }
		);
		assertEquals(_parser.optionExists("-a"), false);
		assertEquals(_parser.getOptionValue("-a"), null);
		assertEquals(_parser.optionExists("-b"), false);
		assertEquals(_parser.getOptionValue("-b"), null);
		assertEquals(_parser.optionExists("-c"), false);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
		assertEquals(_parser.getArgumentValue("LEVEL"), 400);
	}

	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Too few command line arguments\\. Expected min: 2 but was: 1\\."
	)
	public void noOptionsAndTooFewArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "100" }
		);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Too many command line arguments\\. Expected max: 4 but was: 5\\."
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
	public void noOptionsAndMandatoryArgumentsOnly()
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
		assertEquals(_parser.getArgumentValue("LEVEL"), -400);
	}
	
	@Test
	public void noOptionsAndMandatoryArgumentsAndOneOptionalArgument()
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
		assertEquals(_parser.getArgumentValue("LEVEL"), -400);
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
			new String[] { "-a", "-b22", "-c", "100", "200", "300", "400" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), -11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
		assertEquals(_parser.getArgumentValue("LEVEL"), 400);
	}
	
	@Test
	public void allOptionsAWithDefaultArgumentAndMandatoryArgumentsOnly()
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
		assertEquals(_parser.getArgumentValue("LEVEL"), -400);
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
			new String[] { "-a11", "100", "200", "300", "400" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), false);
		assertEquals(_parser.getOptionValue("-b"), null);
		assertEquals(_parser.optionExists("-c"), false);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
		assertEquals(_parser.getArgumentValue("LEVEL"), 400);
	}
	
	@Test
	public void optionAOnlyAndMandatoryArgumentsOnly()
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
		assertEquals(_parser.getArgumentValue("LEVEL"), -400);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Too few command line arguments\\. Expected min: 2 but was: 1\\."
	)
	public void optionAWithDefaultArgumentAndMandatoryArgumentsOnly()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-a", "100", "200" }
		);
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
			new String[] { "-a", "100", "200", "300", "400" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 100);
		assertEquals(_parser.optionExists("-b"), false);
		assertEquals(_parser.getOptionValue("-b"), null);
		assertEquals(_parser.optionExists("-c"), false);
		assertEquals(_parser.getArgumentValue("PID"), 200);
		assertEquals(_parser.getArgumentValue("TYPE"), 300);
		assertEquals(_parser.getArgumentValue("ACTION"), 400);
		assertEquals(_parser.getArgumentValue("LEVEL"), -400);
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
			new String[] { "-b", "22", "-c", "100", "200", "300", "400", "-a", "11" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
		assertEquals(_parser.getArgumentValue("LEVEL"), 400);
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
			new String[] { "-b", "22", "100", "200", "300", "400", "-a", "11", "-c" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
		assertEquals(_parser.getArgumentValue("LEVEL"), 400);
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
			new String[] { "100", "200", "300", "400", "-a", "-b", "22", "-c" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), -11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
		assertEquals(_parser.getArgumentValue("LEVEL"), 400);
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
			new String[] { "-c", "100", "200", "300", "400", "-a", "11", "-b", "22" }
		);
		assertEquals(_parser.optionExists("-a"), true);
		assertEquals(_parser.getOptionValue("-a"), 11);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), 22);
		assertEquals(_parser.optionExists("-c"), true);
		assertEquals(_parser.getArgumentValue("PID"), 100);
		assertEquals(_parser.getArgumentValue("TYPE"), 200);
		assertEquals(_parser.getArgumentValue("ACTION"), 300);
		assertEquals(_parser.getArgumentValue("LEVEL"), 400);
	}
	
	@Test
	public void someOptionsAfterArgumentsAndMandatoryArgumentsOnlyAWithDefaultValue()
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
		assertEquals(_parser.getArgumentValue("LEVEL"), -400);
	}
	
	@Test
	public void someOptionsAfterArgumentsAndMandatoryArgumentsAndOneOptionalArgumentAndOnlyAWithDefaultValue()
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
		assertEquals(_parser.getArgumentValue("LEVEL"), -400);
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
			"Too few command line arguments\\. Expected min: 2 but was: 1\\."
				+ " Check that there are no options between arguments\\."
	)
	public void optionBBetweenArgumentsAndMandatoryArgumentsOnly3()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "100", "-b22", "200", "300", "400" }
		);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Too few command line arguments\\. Expected min: 2 but was: 1\\."
				+ " Check that there are no options between arguments\\."
	)
	public void optionCBetweenArgumentsAndMandatoryArgumentsOnly()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "100", "-c", "200", "300", "400" }
		);
	}
	
}

