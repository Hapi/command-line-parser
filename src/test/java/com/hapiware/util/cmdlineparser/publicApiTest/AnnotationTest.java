package com.hapiware.util.cmdlineparser.publicApiTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
import com.hapiware.util.cmdlineparser.annotation.Id;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;

public class AnnotationTest
	extends
		TestBase
{
	private CommandLineParser _parser;
	
	@Id("PID") private int _pid;
	@Id("b") private int _b;
	@Id("g") private boolean _global;
	@Id("global-int") private int _globalInt;
	

	@BeforeMethod
	public void init()
	{
		_pid = -999;
		_b = -999;
		_global = false;
		_globalInt = -999;
		_parser =
			new CommandLineParser(
				AnnotationTest.class,
				new Description().description("Main description.")
			);
		_parser.add(new Option("g") {{
			alternatives("global");
			description("Global description.");
		}});
		_parser.add(new Option("global-int") {{
			set(
				Integer.class,
				new OptionArgument<Integer>()
			);
			description("Global integer description.");
		}});
		_parser.add(new Command("set", "set description") {{
			description("Description");
			add(Integer.class, new Argument<Integer>("PID") {{
				description("PID description");
				minValue(-2);
				maxValue(150);
			}});
			add(new Option("b") {{
				description("Description");
				set(Integer.class, new OptionArgument<Integer>() {{
					minValue(-3);
					maxValue(6);
				}});
			}});
		}});
		_parser.add(
			new Command(
				"test",
				"test description",
				new TestExecutor()
			) {{
				description("Description");
				add(Integer.class, new Argument<Integer>("UID") {{
					description("UID description");
					minValue(1000);
					maxValue(1010);
				}});
				add(new Option("v") {{
					alternatives("verbose");
					description("Description");
				}});
				add(new Option("d") {{
					description("Description");
					set(Integer.class, new OptionArgument<Integer>() {{
						minValue(-3);
						maxValue(6);
					}});
				}});
			}}
		);
	}

	@Test
	public void baseCase()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "--global", "--global-int", "616", "set", "-b", "-2", "123" }
		);
		assertEquals(_parser.getOptionValue("--global-int"), 616);
		assertTrue(_parser.optionExists("--global"));
		assertEquals(_parser.getCommand().getArgumentValue("PID"), 123);
		assertEquals(_parser.getCommand().getOptionValue("-b"), -2);
		assertEquals(_globalInt, 616);
		assertTrue(_global);
		assertEquals(_pid, 123);
		assertEquals(_b, -2);
	}
	
	@Test
	public void executorCase()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			this,
			new String[] { "--global", "--global-int", "313", "test", "--verbose", "1001" , "-d", "4" }
		);
		assertEquals(_parser.getOptionValue("--global-int"), 313);
		assertTrue(_parser.optionExists("--global"));
		assertEquals(_parser.getCommand().getArgumentValue("UID"), 1001);
		assertEquals(_parser.getCommand().getOptionValue("-d"), 4);
		assertEquals(_globalInt, 313);
		assertTrue(_global);
	}
	
	static class TestExecutor implements CommandExecutor
	{
		@Id("g") private boolean _global;
		@Id("global-int") private Integer _gi;
		@Id("v") private boolean _verbose;
		@Id("d") private int _d;
		
		public void execute(Data command, List<Option.Data> globalOptions)
		{
			assertEquals(command.getOptionValue("-d"), 4);
			assertTrue(command.optionExists("-v"));
			assertEquals(_d, 4);
			assertTrue(_verbose);
			assertTrue(_global);
			assertEquals(_gi, new Integer(313));
		}
		
	}
}
