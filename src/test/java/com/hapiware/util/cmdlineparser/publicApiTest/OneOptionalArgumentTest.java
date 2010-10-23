package com.hapiware.util.cmdlineparser.publicApiTest;

import static junit.framework.Assert.assertEquals;

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


public class OneOptionalArgumentTest
	extends
		TestBase
{
	private CommandLineParser _parser;
	
	@BeforeMethod
	public void init()
	{
		_parser =
			new CommandLineParser(
				OneOptionalArgumentTest.class,
				new Description().description("Main description.")
			);
		_parser.add(Integer.class, new Argument<Integer>("PID") {{
			description("Description for PID.");
		}});
		_parser.add(Integer.class, new Argument<Integer>("TYPE") {{
			optional(-200);
			description("Description for TYPE.");
		}});
		_parser.add(Integer.class, new Argument<Integer>("ACTION") {{
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
		assertEquals(true, _parser.optionExists("-a"));
		assertEquals(11, _parser.getOptionValue("-a"));
		assertEquals(true, _parser.optionExists("-b"));
		assertEquals(22, _parser.getOptionValue("-b"));
		assertEquals(true, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
		
		_parser.parse(
			new String[] { "-a", "11", "-b", "22", "-c", "100", "200", "300" }
		);
		assertEquals(true, _parser.optionExists("-a"));
		assertEquals(11, _parser.getOptionValue("-a"));
		assertEquals(true, _parser.optionExists("-b"));
		assertEquals(22, _parser.getOptionValue("-b"));
		assertEquals(true, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
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
		assertEquals(false, _parser.optionExists("-a"));
		assertEquals(null, _parser.getOptionValue("-a"));
		assertEquals(false, _parser.optionExists("-b"));
		assertEquals(null, _parser.getOptionValue("-b"));
		assertEquals(false, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Too few command line arguments\\. Expected min: 2 but was: 1"
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
			"Too many command line arguments\\. Expected max: 3 but was: 4"
	)
	public void noOptionsAndTooManyArguments()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "100", "200", "300", "400" }
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
			new String[] { "100", "300" }
		);
		assertEquals(false, _parser.optionExists("-a"));
		assertEquals(null, _parser.getOptionValue("-a"));
		assertEquals(false, _parser.optionExists("-b"));
		assertEquals(null, _parser.getOptionValue("-b"));
		assertEquals(false, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(-200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
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
		assertEquals(true, _parser.optionExists("-a"));
		assertEquals(-11, _parser.getOptionValue("-a"));
		assertEquals(true, _parser.optionExists("-b"));
		assertEquals(22, _parser.getOptionValue("-b"));
		assertEquals(true, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
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
			new String[] { "-a", "-b22", "-c", "100", "300" }
		);
		assertEquals(true, _parser.optionExists("-a"));
		assertEquals(-11, _parser.getOptionValue("-a"));
		assertEquals(true, _parser.optionExists("-b"));
		assertEquals(22, _parser.getOptionValue("-b"));
		assertEquals(true, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(-200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
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
		assertEquals(true, _parser.optionExists("-a"));
		assertEquals(11, _parser.getOptionValue("-a"));
		assertEquals(false, _parser.optionExists("-b"));
		assertEquals(null, _parser.getOptionValue("-b"));
		assertEquals(false, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
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
			new String[] { "-a11", "100", "300" }
		);
		assertEquals(true, _parser.optionExists("-a"));
		assertEquals(11, _parser.getOptionValue("-a"));
		assertEquals(false, _parser.optionExists("-b"));
		assertEquals(null, _parser.getOptionValue("-b"));
		assertEquals(false, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(-200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
	}
	
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class },
		expectedExceptionsMessageRegExp =
			"Too few command line arguments\\. Expected min: 2 but was: 1"
	)
	public void optionAWithDefaultArgumentAndMandatoryArgumentsOnly()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-a", "100", "300" }
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
			new String[] { "-a", "100", "200", "300" }
		);
		assertEquals(true, _parser.optionExists("-a"));
		assertEquals(100, _parser.getOptionValue("-a"));
		assertEquals(false, _parser.optionExists("-b"));
		assertEquals(null, _parser.getOptionValue("-b"));
		assertEquals(false, _parser.optionExists("-c"));
		assertEquals(200, _parser.getArgumentValue("PID"));
		assertEquals(-200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
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
		assertEquals(true, _parser.optionExists("-a"));
		assertEquals(11, _parser.getOptionValue("-a"));
		assertEquals(true, _parser.optionExists("-b"));
		assertEquals(22, _parser.getOptionValue("-b"));
		assertEquals(true, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
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
		assertEquals(true, _parser.optionExists("-a"));
		assertEquals(11, _parser.getOptionValue("-a"));
		assertEquals(true, _parser.optionExists("-b"));
		assertEquals(22, _parser.getOptionValue("-b"));
		assertEquals(true, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
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
		assertEquals(true, _parser.optionExists("-a"));
		assertEquals(-11, _parser.getOptionValue("-a"));
		assertEquals(true, _parser.optionExists("-b"));
		assertEquals(22, _parser.getOptionValue("-b"));
		assertEquals(true, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
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
		assertEquals(true, _parser.optionExists("-a"));
		assertEquals(11, _parser.getOptionValue("-a"));
		assertEquals(true, _parser.optionExists("-b"));
		assertEquals(22, _parser.getOptionValue("-b"));
		assertEquals(true, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
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
			new String[] { "-b", "22", "100", "300", "-a", "-c" }
		);
		assertEquals(true, _parser.optionExists("-a"));
		assertEquals(-11, _parser.getOptionValue("-a"));
		assertEquals(true, _parser.optionExists("-b"));
		assertEquals(22, _parser.getOptionValue("-b"));
		assertEquals(true, _parser.optionExists("-c"));
		assertEquals(100, _parser.getArgumentValue("PID"));
		assertEquals(-200, _parser.getArgumentValue("TYPE"));
		assertEquals(300, _parser.getArgumentValue("ACTION"));
	}
	
	// TODO: Test options between arguments.
	@Test(
		expectedExceptions = { IllegalCommandLineArgumentException.class }
		//expectedExceptionsMessageRegExp =
			//"" // TODO: Wrong message!!!
	)
	public void optionCBetweenArgumentsAndMandatoryArgumentsOnly()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		// TODO: The system does not recognize options between arguments (which is illegal). Figure out the algorithm.
		_parser.parse(
			new String[] { "100", "200", "-b22", "300" }
		);
	}
	
}
