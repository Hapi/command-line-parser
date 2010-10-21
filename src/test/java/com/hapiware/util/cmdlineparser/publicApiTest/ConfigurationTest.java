package com.hapiware.util.cmdlineparser.publicApiTest;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hapiware.util.cmdlineparser.Argument;
import com.hapiware.util.cmdlineparser.Command;
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.ConfigurationException;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.Option;


public class ConfigurationTest
	extends
		TestBase
{
	private CommandLineParser _parser;
	
	@BeforeMethod
	public void init()
	{
		_parser = 
			new CommandLineParser(
				ConfigurationTest.class,
				new Description().description("Main description.")
			);
	}
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Option name '-a' must be unique\\."
	)
	public void duplicateOptionNames()
	{
		_parser.add(new Option("a") {{
			description("Description for a.");
		}});
		_parser.add(new Option("b") {{
			description("Description for b.");
		}});
		_parser.add(new Option("a") {{
			description("DUPLICATE for a.");
		}});
	}
	
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Argument name 'PID' must be unique\\."
	)
	public void duplicateArgumentNames()
	{
		_parser.add(Integer.class, new Argument<Integer>("PID") {{
			description("Description for PID.");
		}});
		_parser.add(Byte.class, new Argument<Byte>("TYPE") {{
			description("Description for TYPE.");
		}});
		_parser.add(String.class, new Argument<String>("PID") {{
			description("DUPLICATE for PID.");
		}});
	}
	
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Command name 'get' must be unique\\."
	)
	public void duplicateCommandNames()
	{
		_parser.add(new Command("get", "Short desc for get") {{
			description("Description for get");
		}});
		_parser.add(new Command("set", "Short desc for set") {{
			description("Description for get");
		}});
		_parser.add(new Command("get", "Short desc for get") {{
			description("DUPLICATE for get");
		}});
	}
	
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Option name '-a' for command 'get' must be unique\\."
	)
	public void duplicateCommandOptionNames()
	{
		_parser.add(new Command("get", "Short desc for get") {{
			description("Description for get");
			add(new Option("a") {{
				description("Description for option '-a'.");
			}});
			add(new Option("b") {{
				description("Description for option '-b'.");
			}});
			add(new Option("a") {{
				description("DUPLICATE for option '-a'.");
			}});
		}});
	}
	
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Argument name 'PID' for command 'get' must be unique\\."
	)
	public void duplicateCommandArgumentNames()
	{
		_parser.add(new Command("get", "Short desc for get") {{
			description("Description for get");
			add(Integer.class, new Argument<Integer>("PID") {{
				description("Description for PID.");
			}});
			add(Byte.class, new Argument<Byte>("TYPE") {{
				description("Description for TYPE.");
			}});
			add(String.class, new Argument<String>("PID") {{
				description("DUPLICATE for PID.");
			}});
		}});
	}
	
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"All the alternative names for option '-a' must be unique\\. "
				+ "Conflicting alternative name is '--aa'\\."
	)
	public void duplicateOptionAlternativesSameAlternativesConflict()
	{
		_parser.add(new Option("a") {{
			alternatives("aa", "aaaa", "aa");
			description("Description for a.");
		}});
	}
	
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Option alternative name '--aa' must be unique\\."
	)
	public void duplicateOptionAlternativesNameAlternativeConflict()
	{
		_parser.add(new Option("aa") {{
			alternatives("a", "aaaa", "aa");
			description("Description for a.");
		}});
	}
	
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Option name '--conflict' must be unique\\."
	)
	public void duplicateOptionAlternativesOtherOptionNameConflict()
	{
		_parser.add(new Option("a") {{
			alternatives("aa", "aaaa", "conflict");
			description("Description for a.");
		}});
		_parser.add(new Option("conflict") {{
			description("Description for conflict.");
		}});
	}


	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Option alternative name '--conflict' must be unique\\."
	)
	public void duplicateOptionAlternativesOtherOptionAlternativeConflict()
	{
		_parser.add(new Option("a") {{
			alternatives("aa", "aaaa", "conflict");
			description("Description for a.");
		}});
		_parser.add(new Option("b") {{
			alternatives("bb", "bbbb", "conflict");
			description("Description for conflict.");
		}});
	}


	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"All the alternative names for command 's' must be unique\\. "
				+ "Conflicting alternative name is 'set'\\."
	)
	public void duplicateCommandAlternativesSameAlternativesConflict()
	{
		_parser.add(new Command("s", "Short desc. for s.") {{
			alternatives("set", "SET", "set");
			description("Description for s.");
		}});
	}
	
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Command alternative name 'set' must be unique\\."
	)
	public void duplicateCommandAlternativesNameAlternativeConflict()
	{
		_parser.add(new Command("set", "Short desc. for set.") {{
			alternatives("s", "SET", "set");
			description("Description for s.");
		}});
	}
	
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Command name 'conflict' must be unique\\."
	)
	public void duplicateCommandAlternativesOtherOptionNameConflict()
	{
		_parser.add(new Command("s", "Short desc. for s.") {{
			alternatives("set", "SET", "conflict");
			description("Description for s.");
		}});
		_parser.add(new Command("conflict", "Short desc. for conflict.") {{
			description("Description for conflict.");
		}});
	}
	
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Command alternative name 'conflict' must be unique\\."
	)
	public void duplicateCommandAlternativesOtherOptionAlternativeConflict()
	{
		_parser.add(new Command("s", "Short desc. for s.") {{
			alternatives("set", "SET", "conflict");
			description("Description for s.");
		}});
		_parser.add(new Command("g", "Short desc. for g.") {{
			alternatives("get", "GET", "conflict");
			description("Description for conflict.");
		}});
	}

	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"All the alternative names for option '-a' must be unique\\. "
				+ "Conflicting alternative name is '--aa'\\."
	)
	public void duplicateCommandOptionAlternativesSameAlternativesConflict()
	{
		_parser.add(new Command("set", "Short desc. for set.") {{
			add(new Option("a") {{
				alternatives("aa", "aaaa", "aa");
				description("Description for a.");
			}});
		}});
	}
	
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Option alternative name '--aa' for command 'set' must be unique\\."
	)
	public void duplicateCommandOptionAlternativesNameAlternativeConflict()
	{
		_parser.add(new Command("set", "Short desc. for set.") {{
			add(new Option("aa") {{
				alternatives("a", "aaaa", "aa");
				description("Description for a.");
			}});
		}});
	}
	
	
	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Option name '--conflict' for command 'set' must be unique\\."
	)
	public void duplicateCommandOptionAlternativesOtherOptionNameConflict()
	{
		_parser.add(new Command("set", "Short desc. for set.") {{
			add(new Option("a") {{
				alternatives("aa", "aaaa", "conflict");
				description("Description for a.");
			}});
			add(new Option("conflict") {{
				description("Description for conflict.");
			}});
		}});
	}


	@Test(
		expectedExceptions = {ConfigurationException.class},
		expectedExceptionsMessageRegExp =
			"Option alternative name '--conflict' for command 'set' must be unique\\."
	)
	public void duplicateCommandOptionAlternativesOtherOptionAlternativeConflict()
	{
		_parser.add(new Command("set", "Short desc. for set.") {{
			add(new Option("a") {{
				alternatives("aa", "aaaa", "conflict");
				description("Description for a.");
			}});
			add(new Option("b") {{
				alternatives("bb", "bbbb", "conflict");
				description("Description for conflict.");
			}});
		}});
	}
}
