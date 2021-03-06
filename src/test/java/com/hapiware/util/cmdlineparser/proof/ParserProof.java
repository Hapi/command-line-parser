package com.hapiware.util.cmdlineparser.proof;

import com.hapiware.util.cmdlineparser.Argument;
import com.hapiware.util.cmdlineparser.Command;
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.Option;
import com.hapiware.util.cmdlineparser.OptionArgument;
import com.hapiware.util.cmdlineparser.annotation.Id;
import com.hapiware.util.cmdlineparser.constraint.Enumeration;
import com.hapiware.util.cmdlineparser.publicApiTest.TestBase;

public class ParserProof
	extends
		TestBase
{
	@Id("n")
	private static int _a;

	@Id("miu")
	private static boolean _miu;
	
	@Id("s")
	private static String[] _numba;
	
	@Id("cmd")
	private static String _cmd;
	
	@Id("TYPE")
	private static int _type;

	@Id("TYPE")
	private static int _type2;
	
	public static void main(String[] args) throws Throwable
	{
		replacePackage(ParserProof.class);
		CommandLineParser p =
			new CommandLineParser(
				ParserProof.class,
				new Description().description("Main description.").p().description("Something else.")
			);
		p.add(new Option("m") {{
			alternatives("moi").id("miu");
			description("dflasjdfa lsfj");
		}});
		p.add(new Option("n") {{
			//multiple();
			alternatives("number");
			description("Description for ").strong("number").description(" option.");
			set(Integer.class, new OptionArgument<Integer>() {{
				optional(5);
				minValue(1);
				maxValue(1000);
			}});
		}});
		p.add(new Option("s") {{
			multiple();
			description("Description");
			set(String.class, new OptionArgument<String>() {{
				maxLength(5);
			}});
		}});
		p.add(new Option("d") {{
			description("Description");
			set(String.class, new OptionArgument<String>("loggerType") {{
				//maxLength(5);
				constraint(new Enumeration<String>() {{
					valueIgnoreCase("J", "for Java loggers.");
					value("4", "for log4j loggers.");
				}});
			}});
		}});
		
		p.add(new Command("set", "Short desc.") {{
			alternatives("s").id("cmd");
			description("Description");
			add(Integer.class, new Argument<Integer>("PID") {{
				description("Process ID for JVM.");
			}});
			add(Integer.class, new Argument<Integer>("TYPE") {{
				optional(4);
				//maxLength(5);
				minValue(3);
				maxValue(12);
				constraint(new Enumeration<Integer>() {{
					value(3, "for three");
					value(7, "for seven");
					includeRange(1, 18, "moro poro");
					excludeRange(2, 5, "moro poro");
				}});
				description("....");
			}});
			add(Integer.class, new Argument<Integer>("RE") {{
				description("Regular expression.");
			}});
			add(String.class, new Argument<String>("LEVEL") {{
				description("New logging level.");
			}});
			add(new Option("v") {{
				description("Description");
			}});
		}});
		p.addExampleArguments("set 1234 j ^.+ INFO");
		
		p.printCompleteHelp();
		//p.printShortHelp();
		//p.printGlobalOptionsHelp();
		//p.printCommandsHelp();
		//p.printCommandHelp("set");
		/*System.out.println();
		System.out.println("-----------------------------------");
		System.out.println();*/
		
		String[] arguments =
			new String[] { "-sMorop", "-sMiu", "--moi", "-d", "j", "set", "123", "3", "2", "level", "--number", "1000" };
			//new String[] { "-?" };
			//new String[] { "--help", "cmd=set" };
			//new String[] { "-?", "all" };
			//new String[] { "--version" };
		
		p.parseInternalOptions(arguments);
		
		// Do something here before the real parsing like reading a configuration file etc.
		System.out.println("Read a configuration file...\n");
		
		p.parsech(arguments);
		
		System.out.println(p.optionExists("-m"));
		System.out.println(p.optionExists("-a"));
		System.out.println(p.getOptionValue("-m"));
		System.out.println(p.getOptionValue("-n"));
	
		System.out.println("_miu = " + _miu);
		System.out.println("_a = " + _a);
		System.out.println("_numba = " + _numba[0]);
		System.out.println("cmd = " + _cmd);
		System.out.println("type = " + _type);
		System.out.println("type2 = " + _type2);
		System.out.println("Success.");
	}
}
