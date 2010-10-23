package com.hapiware.util.cmdlineparser.publicApiTest;

import static junit.framework.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;

import com.hapiware.util.cmdlineparser.Argument;
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.Description;


public class ConstraintTest
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
		_parser.add(Integer.class, new Argument<Integer>("PID") {{
			description("Description for PID.");
		}});
		_parser.add(Integer.class, new Argument<Integer>("TYPE") {{
			optional(0);
			description("Description for TYPE.");
		}});
		_parser.add(Integer.class, new Argument<Integer>("ACTION") {{
			description("Description for ACTION.");
		}});

		// TODO: Remove this.
		assertEquals(true, true);
	}
	
	// TODO: Add tests.
}
