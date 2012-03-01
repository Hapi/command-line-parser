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
import com.hapiware.util.cmdlineparser.constraint.Constraint;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;
import com.hapiware.util.cmdlineparser.constraint.MinLength;


public class TwoOptionalArgumentsWithCustomConstraintsTest extends TestBase
{
	private CommandLineParser _parser;

	@BeforeMethod
	public void init()
	{
		_parser =
			new CommandLineParser(
				TwoOptionalArgumentsWithCustomConstraintsTest.class,
				new Description().description("Main description.")
			);
		_parser.add(String.class, new Argument<String>("URL") {{
			description("Description for URL.");
			constraint(new MinLength(5));
		}});
		_parser.add(String.class, new Argument<String>("EARLIEST") {{
			description("Description for EARLIEST.");
			optional("", false);
			constraint(new CustomConstraint());
		}});
		_parser.add(String.class, new Argument<String>("LATEST") {{
			description("Description for LATEST.");
			optional("", false);
			constraint(new CustomConstraint());
		}});
		_parser.add(new Option("user") {{
			alternatives("u");
			description("Description for --user.");
			set(String.class, new OptionArgument<String>().optional("", true));
		}});
		_parser.add(new Option("password") {{
			alternatives("p");
			description("Description for --password.");
			set(String.class, new OptionArgument<String>().optional("", true));
		}});

		_parser.add(new Option("batch") {{
			alternatives("b");
			description("Description for --batch.");
			set(String.class, new OptionArgument<String>().optional("filename.txt", true));
		}});
		_parser.add(new Option("output") {{
			alternatives("o");
			description("Description for --output.");
			set(String.class, new OptionArgument<String>());
		}});
	}
	
	@Test
	public void allArgumentsFirst() 
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "http://localhost:8080", "2012-01-01", "2012-01-20", "--user", "admin", "-b", "-p", "adminpwd" }
		);
		assertEquals(_parser.optionExists("--user"), true);
		assertEquals(_parser.getOptionValue("--user"), "admin");
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), "filename.txt");
		assertEquals(_parser.optionExists("-p"), true);
		assertEquals(_parser.getOptionValue("-p"), "adminpwd");
		assertEquals(_parser.getArgumentValue("URL"), "http://localhost:8080");
		assertEquals(_parser.getArgumentValue("EARLIEST"), "2012-01-01");
		assertEquals(_parser.getArgumentValue("LATEST"), "2012-01-20");
	}
	
	@Test
	public void allOptionsFirst() 
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "--user", "admin", "-b", "-p", "adminpwd", "http://localhost:8080" }
		);
		assertEquals(_parser.optionExists("--user"), true);
		assertEquals(_parser.getOptionValue("--user"), "admin");
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), "filename.txt");
		assertEquals(_parser.optionExists("-p"), true);
		assertEquals(_parser.getOptionValue("-p"), "adminpwd");
		assertEquals(_parser.getArgumentValue("URL"), "http://localhost:8080");
		assertEquals(_parser.getArgumentValue("EARLIEST"), "");
		assertEquals(_parser.getArgumentValue("LATEST"), "");
	}
	
	@Test
	public void optionsAndArgumentsMixed() 
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "--user", "admin", "http://localhost:8080", "2012-01-01", "2012-01-20", "-b", "-p", "adminpwd" }
		);
		assertEquals(_parser.optionExists("--user"), true);
		assertEquals(_parser.getOptionValue("--user"), "admin");
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), "filename.txt");
		assertEquals(_parser.optionExists("-p"), true);
		assertEquals(_parser.getOptionValue("-p"), "adminpwd");
		assertEquals(_parser.getArgumentValue("URL"), "http://localhost:8080");
		assertEquals(_parser.getArgumentValue("EARLIEST"), "2012-01-01");
		assertEquals(_parser.getArgumentValue("LATEST"), "2012-01-20");
	}
	
	@Test
	public void allArgumentsOnlyBatchOption() 
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "http://localhost:8080", "2012-01-01", "2012-01-20", "-b" ,".secret-file=moro.txt" }
		);
		assertEquals(_parser.optionExists("--user"), false);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), ".secret-file=moro.txt");
		assertEquals(_parser.optionExists("-p"), false);
		assertEquals(_parser.getArgumentValue("URL"), "http://localhost:8080");
		assertEquals(_parser.getArgumentValue("EARLIEST"), "2012-01-01");
		assertEquals(_parser.getArgumentValue("LATEST"), "2012-01-20");
	}
	
	@Test
	public void allArgumentsOnlyBatchOptionConcatenatedWithArgument() 
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-b.secret-file=moro.txt", "http://localhost:8080", "2012-01-01", "2012-01-20" }
		);
		assertEquals(_parser.optionExists("--user"), false);
		assertEquals(_parser.optionExists("-b"), true);
		assertEquals(_parser.getOptionValue("-b"), ".secret-file=moro.txt");
		assertEquals(_parser.optionExists("-p"), false);
		assertEquals(_parser.getArgumentValue("URL"), "http://localhost:8080");
		assertEquals(_parser.getArgumentValue("EARLIEST"), "2012-01-01");
		assertEquals(_parser.getArgumentValue("LATEST"), "2012-01-20");
	}
}


class CustomConstraint
	implements
		Constraint<String>
{

	public boolean typeCheck(Class<?> typeClass)
	{
		return typeClass == String.class;
	}

	public void evaluate(String argumentName, String value) throws ConstraintException
	{
		// Does nothing.
	}

	public Description description()
	{
		return new Description().description("Constraint must be...");
	}
}
