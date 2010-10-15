package com.hapiware.utils.cmdline;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hapiware.utils.cmdline.annotation.Id;
import com.hapiware.utils.cmdline.constraint.AnnotatedFieldSetException;
import com.hapiware.utils.cmdline.constraint.CommandNotFoundException;
import com.hapiware.utils.cmdline.constraint.ConstraintException;
import com.hapiware.utils.cmdline.constraint.IllegalCommandLineArgumentException;
import com.hapiware.utils.cmdline.element.Argument;
import com.hapiware.utils.cmdline.element.Command;
import com.hapiware.utils.cmdline.element.Description;
import com.hapiware.utils.cmdline.element.Option;
import com.hapiware.utils.cmdline.element.OptionArgument;


public class ExceptionTest
{
	private CommandLineParser _parser;
	
	@SuppressWarnings("unused")
	@Id("type-mismatch")
	private byte _typeMismatch;
	
	@SuppressWarnings("unused")
	@Id("a")
	private String _a;
	
	@BeforeMethod
	public void init()
	{
		_parser =
			new CommandLineParser(
				ParserTest.class,
				new Description().description("Main description.")
			);
		_parser.add(new Option("type-mismatch") {{
			description("Description");
			set(Integer.class, new OptionArgument<Integer>() {{
				minValue(1);
				maxValue(10);
			}});
		}});
		_parser.add(new Command("set", "set description") {{
			description("Description");
			add(Integer.class, new Argument<Integer>("PID") {{
				description("PID");
				minValue(-2);
				maxValue(150);
			}});
			add(new Option("a") {{
				description("Description");
				set(String.class, new OptionArgument<String>() {{
					minLength(3);
					maxLength(6);
				}});
			}});
		}});
	}

	@Test(
		expectedExceptions = {AnnotatedFieldSetException.class},
		expectedExceptionsMessageRegExp =
			".*\\[2\\] is an illegal argument for the field annotated 'type-mismatch'.*"
	)
	public void annotatedSetFailure()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(this, new String[] { "--type-mismatch", "2", "set", "-a", "abc", "123" });
	}
	
	@Test(
		expectedExceptions = {CommandNotFoundException.class},
		expectedExceptionsMessageRegExp =
			"A command was expected but 'missing-command' cannot be interpreted as a command."
	)
	public void missingCommand()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(this, new String[] { "missing-command", "-a", "abc", "123" });
	}
	
	@Test(
		expectedExceptions = {ConstraintException.class},
		expectedExceptionsMessageRegExp =
			"Length of \\[ab\\] is shorter than the minimum length 3 allowed for '-a'"
	)
	public void tooShortStringLength()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(this, new String[] { "set", "-a", "ab", "123" });
	}

	@Test(
		expectedExceptions = {ConstraintException.class},
		expectedExceptionsMessageRegExp =
			"Length of \\[abcdefg\\] is longer than the maximum length 6 allowed for '-a'"
	)
	public void tooLongStringLength()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(this, new String[] { "set", "-a", "abcdefg", "123" });
	}

	@Test(
		expectedExceptions = {IllegalCommandLineArgumentException.class},
		expectedExceptionsMessageRegExp =
			"\\[123a\\] cannot be interpreted as java.lang.Integer for 'PID'"
	)
	public void wrongCommandArgument()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(this, new String[] { "set", "-a", "abc", "123a" });
	}
	
	@Test(
		expectedExceptions = {ConstraintException.class},
		expectedExceptionsMessageRegExp =
			"\\[-31\\] is smaller than the minimum value -2 allowed for 'PID'"
	)
	public void tooSmallValue()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(this, new String[] { "set", "-31" });
	}

	@Test(
		expectedExceptions = {ConstraintException.class},
		expectedExceptionsMessageRegExp =
			"\\[1234\\] is greater than the maximum value 150 allowed for 'PID'"
	)
	public void tooBigValue()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(this, new String[] { "set", "1234" });
	}
}
