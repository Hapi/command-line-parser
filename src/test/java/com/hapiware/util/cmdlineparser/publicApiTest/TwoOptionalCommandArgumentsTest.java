package com.hapiware.util.cmdlineparser.publicApiTest;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hapiware.util.cmdlineparser.AnnotatedFieldSetException;
import com.hapiware.util.cmdlineparser.Argument;
import com.hapiware.util.cmdlineparser.Command;
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.CommandNotFoundException;
import com.hapiware.util.cmdlineparser.ConfigurationException;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.IllegalCommandLineArgumentException;
import com.hapiware.util.cmdlineparser.Option;
import com.hapiware.util.cmdlineparser.OptionArgument;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;

public class TwoOptionalCommandArgumentsTest
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
		_parser.add(new Command("set", "Short desc for set") {{
			description("description");
			add(Integer.class, new Argument<Integer>("PID") {{
				description("Description for PID.");
			}});
			add(Integer.class, new Argument<Integer>("TYPE") {{
				description("Description for TYPE.");
			}});
			add(Integer.class, new Argument<Integer>("ACTION") {{
				optional(-300);
				description("Description for ACTION.");
			}});
			add(Integer.class, new Argument<Integer>("LEVEL") {{
				optional(-400);
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

	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp = 
			"If there is more than one optional argument they must be the last arguments\\. "
				+ "The first conflicting argument for command 'set' is 'LEVEL'\\. "
				+ "A single optional argument can have any position\\."
	)
	public void misplacedOptionalArguments()
	{
		CommandLineParser p =
			new CommandLineParser(
				TwoOptionalCommandArgumentsTest.class,
				new Description().description("Main description.")
			);
		p.add(new Command("set", "Short desc for set") {{
			description("description");
			add(Integer.class, new Argument<Integer>("PID") {{
				description("Description for PID.");
			}});
			add(Integer.class, new Argument<Integer>("TYPE") {{
				optional(-200);
				description("Description for TYPE.");
			}});
			add(Integer.class, new Argument<Integer>("ACTION") {{
				optional(-300);
				description("Description for ACTION.");
			}});
			add(Integer.class, new Argument<Integer>("LEVEL") {{
				description("Description for LEVEL.");
			}});
		}});
	}
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp = 
			"If there is more than one optional argument they must be the last arguments\\. "
				+ "The first conflicting argument for command 'set' is 'LEVEL'\\. "
				+ "A single optional argument can have any position\\."
	)
	public void misplacedOptionalArguments2()
	{
		CommandLineParser p =
			new CommandLineParser(
				TwoOptionalCommandArgumentsTest.class,
				new Description().description("Main description.")
			);
		p.add(new Command("set", "Short desc for set") {{
			add(Integer.class, new Argument<Integer>("PID") {{
				description("Description for PID.");
			}});
			add(Integer.class, new Argument<Integer>("TYPE") {{
				optional(-200);
				description("Description for TYPE.");
			}});
			add(Integer.class, new Argument<Integer>("ACTION") {{
				description("Description for ACTION.");
			}});
			add(Integer.class, new Argument<Integer>("LEVEL") {{
				optional(-400);
				description("Description for LEVEL.");
			}});
		}});
	}
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp = 
			"If there is more than one optional argument they must be the last arguments\\. "
				+ "The first conflicting argument for command 'set' is 'LEVEL'\\. "
				+ "A single optional argument can have any position\\."
	)
	public void misplacedOptionalArguments3()
	{
		CommandLineParser p =
			new CommandLineParser(
				TwoOptionalCommandArgumentsTest.class,
				new Description().description("Main description.")
			);
		p.add(new Command("set", "Short desc for set.") {{
			description("description");
			add(Integer.class, new Argument<Integer>("PID") {{
				description("Description for PID.");
			}});
			add(Integer.class, new Argument<Integer>("TYPE") {{
				optional(-200);
				description("Description for TYPE.");
			}});
			add(Integer.class, new Argument<Integer>("ACTION") {{
				description("Description for ACTION.");
			}});
			add(Integer.class, new Argument<Integer>("LEVEL") {{
				optional(-400);
				description("Description for LEVEL.");
			}});
			add(Integer.class, new Argument<Integer>("INFO") {{
				optional(-500);
				description("Description for INFO.");
			}});
		}});
	}
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp = 
			"If there is more than one optional argument they must be the last arguments\\. "
				+ "The first conflicting argument for command 'set' is 'INFO'\\. "
				+ "A single optional argument can have any position\\."
	)
	public void misplacedOptionalArguments4()
	{
		CommandLineParser p =
			new CommandLineParser(
				TwoOptionalCommandArgumentsTest.class,
				new Description().description("Main description.")
			);
		p.add(new Command("set", "Short desc for set.") {{
			description("description");
			add(Integer.class, new Argument<Integer>("PID") {{
				description("Description for PID.");
			}});
			add(Integer.class, new Argument<Integer>("TYPE") {{
				optional(-200);
				description("Description for TYPE.");
			}});
			add(Integer.class, new Argument<Integer>("ACTION") {{
				optional(-300);
				description("Description for ACTION.");
			}});
			add(Integer.class, new Argument<Integer>("LEVEL") {{
				optional(-400);
				description("Description for LEVEL.");
			}});
			add(Integer.class, new Argument<Integer>("INFO") {{
				description("Description for INFO.");
			}});
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
			new String[] { "set", "-a11", "-b22", "-c", "100", "200", "300", "400" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(11, command.getOptionValue("-a"));
		assertEquals(true, command.optionExists("-b"));
		assertEquals(22, command.getOptionValue("-b"));
		assertEquals(true, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(300, command.getArgumentValue("ACTION"));
		assertEquals(400, command.getArgumentValue("LEVEL"));
		
		_parser.parse(
			new String[] { "set", "-a", "11", "-b", "22", "-c", "100", "200", "300", "400" }
		);
		command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(11, command.getOptionValue("-a"));
		assertEquals(true, command.optionExists("-b"));
		assertEquals(22, command.getOptionValue("-b"));
		assertEquals(true, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(300, command.getArgumentValue("ACTION"));
		assertEquals(400, command.getArgumentValue("LEVEL"));
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
			new String[] { "set", "100", "200", "300", "400" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(false, command.optionExists("-a"));
		assertEquals(null, command.getOptionValue("-a"));
		assertEquals(false, command.optionExists("-b"));
		assertEquals(null, command.getOptionValue("-b"));
		assertEquals(false, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(300, command.getArgumentValue("ACTION"));
		assertEquals(400, command.getArgumentValue("LEVEL"));
	}

	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Too few command line arguments for command 'set'\\. Expected min: 2 but was: 1\\."
	)
	public void noOptionsAndTooFewArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "set", "100" }
		);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Too many command line arguments for command 'set'\\. Expected max: 4 but was: 5\\."
	)
	public void noOptionsAndTooManyArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "set", "100", "200", "300", "400", "500" }
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
			new String[] { "set", "100", "200" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(false, command.optionExists("-a"));
		assertEquals(null, command.getOptionValue("-a"));
		assertEquals(false, command.optionExists("-b"));
		assertEquals(null, command.getOptionValue("-b"));
		assertEquals(false, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(-300, command.getArgumentValue("ACTION"));
		assertEquals(-400, command.getArgumentValue("LEVEL"));
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
			new String[] { "set", "100", "200", "300" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(false, command.optionExists("-a"));
		assertEquals(null, command.getOptionValue("-a"));
		assertEquals(false, command.optionExists("-b"));
		assertEquals(null, command.getOptionValue("-b"));
		assertEquals(false, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(300, command.getArgumentValue("ACTION"));
		assertEquals(-400, command.getArgumentValue("LEVEL"));
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
			new String[] { "set", "-a", "-b22", "-c", "100", "200", "300", "400" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(-11, command.getOptionValue("-a"));
		assertEquals(true, command.optionExists("-b"));
		assertEquals(22, command.getOptionValue("-b"));
		assertEquals(true, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(300, command.getArgumentValue("ACTION"));
		assertEquals(400, command.getArgumentValue("LEVEL"));
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
			new String[] { "set", "-a", "-b22", "-c", "100", "200" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(-11, command.getOptionValue("-a"));
		assertEquals(true, command.optionExists("-b"));
		assertEquals(22, command.getOptionValue("-b"));
		assertEquals(true, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(-300, command.getArgumentValue("ACTION"));
		assertEquals(-400, command.getArgumentValue("LEVEL"));
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
			new String[] { "set", "-a11", "100", "200", "300", "400" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(11, command.getOptionValue("-a"));
		assertEquals(false, command.optionExists("-b"));
		assertEquals(null, command.getOptionValue("-b"));
		assertEquals(false, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(300, command.getArgumentValue("ACTION"));
		assertEquals(400, command.getArgumentValue("LEVEL"));
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
			new String[] { "set", "-a11", "100", "200" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(11, command.getOptionValue("-a"));
		assertEquals(false, command.optionExists("-b"));
		assertEquals(null, command.getOptionValue("-b"));
		assertEquals(false, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(-300, command.getArgumentValue("ACTION"));
		assertEquals(-400, command.getArgumentValue("LEVEL"));
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Too few command line arguments for command 'set'\\. Expected min: 2 but was: 1\\."
	)
	public void optionAWithDefaultArgumentAndMandatoryArgumentsOnly()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "set", "-a", "100", "200" }
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
			new String[] { "set", "-a", "100", "200", "300", "400" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(100, command.getOptionValue("-a"));
		assertEquals(false, command.optionExists("-b"));
		assertEquals(null, command.getOptionValue("-b"));
		assertEquals(false, command.optionExists("-c"));
		assertEquals(200, command.getArgumentValue("PID"));
		assertEquals(300, command.getArgumentValue("TYPE"));
		assertEquals(400, command.getArgumentValue("ACTION"));
		assertEquals(-400, command.getArgumentValue("LEVEL"));
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
			new String[] { "set", "-b", "22", "-c", "100", "200", "300", "400", "-a", "11" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(11, command.getOptionValue("-a"));
		assertEquals(true, command.optionExists("-b"));
		assertEquals(22, command.getOptionValue("-b"));
		assertEquals(true, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(300, command.getArgumentValue("ACTION"));
		assertEquals(400, command.getArgumentValue("LEVEL"));
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
			new String[] { "set", "-b", "22", "100", "200", "300", "400", "-a", "11", "-c" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(11, command.getOptionValue("-a"));
		assertEquals(true, command.optionExists("-b"));
		assertEquals(22, command.getOptionValue("-b"));
		assertEquals(true, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(300, command.getArgumentValue("ACTION"));
		assertEquals(400, command.getArgumentValue("LEVEL"));
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
			new String[] { "set", "-b", "22", "100", "200", "300", "400", "-a", "-c" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(-11, command.getOptionValue("-a"));
		assertEquals(true, command.optionExists("-b"));
		assertEquals(22, command.getOptionValue("-b"));
		assertEquals(true, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(300, command.getArgumentValue("ACTION"));
		assertEquals(400, command.getArgumentValue("LEVEL"));
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
			new String[] { "set", "-c", "100", "200", "300", "400", "-a", "11", "-b", "22" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(11, command.getOptionValue("-a"));
		assertEquals(true, command.optionExists("-b"));
		assertEquals(22, command.getOptionValue("-b"));
		assertEquals(true, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(300, command.getArgumentValue("ACTION"));
		assertEquals(400, command.getArgumentValue("LEVEL"));
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
			new String[] { "set", "-b", "22", "100", "200", "-a", "-c" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(-11, command.getOptionValue("-a"));
		assertEquals(true, command.optionExists("-b"));
		assertEquals(22, command.getOptionValue("-b"));
		assertEquals(true, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(-300, command.getArgumentValue("ACTION"));
		assertEquals(-400, command.getArgumentValue("LEVEL"));
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
			new String[] { "set", "-b", "22", "100", "200", "300", "-a", "-c" }
		);
		Command.Data command = _parser.getCommand();
		assertEquals("set", command.getName());
		assertEquals(true, command.optionExists("-a"));
		assertEquals(-11, command.getOptionValue("-a"));
		assertEquals(true, command.optionExists("-b"));
		assertEquals(22, command.getOptionValue("-b"));
		assertEquals(true, command.optionExists("-c"));
		assertEquals(100, command.getArgumentValue("PID"));
		assertEquals(200, command.getArgumentValue("TYPE"));
		assertEquals(300, command.getArgumentValue("ACTION"));
		assertEquals(-400, command.getArgumentValue("LEVEL"));
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Command line argument '400' for command 'set' cannot be interpreted as a proper "
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
			new String[] { "set", "100", "200", "300", "-b22", "400" }
		);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Command line argument '300' for command 'set' cannot be interpreted as a proper "
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
			new String[] { "set", "100", "200", "-b22", "300", "400" }
		);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Too few command line arguments for command 'set'\\. Expected min: 2 but was: 1\\."
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
			new String[] { "set", "100", "-b22", "200", "300", "400" }
		);
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Too few command line arguments for command 'set'\\. Expected min: 2 but was: 1\\."
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
			new String[] { "set", "100", "-c", "200", "300", "400" }
		);
	}
	
}

