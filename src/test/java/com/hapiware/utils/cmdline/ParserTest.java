package com.hapiware.utils.cmdline;

import com.hapiware.utils.cmdline.annotation.Id;
import com.hapiware.utils.cmdline.constraint.Enumeration;
import com.hapiware.utils.cmdline.element.Argument;
import com.hapiware.utils.cmdline.element.Command;
import com.hapiware.utils.cmdline.element.Description;
import com.hapiware.utils.cmdline.element.Option;
import com.hapiware.utils.cmdline.element.OptionArgument;

public class ParserTest
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

	
	public static void main(String[] args)
	{
		CommandLineParser p =
			new CommandLineParser(
				ParserTest.class,
				new Description().description("Main description.").p().description("Something else.")
			);
		p.add(new Option("m") {{
			alternatives("moi").id("miu");
			description("dfšlasjdfa lsfj");
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
			set(String.class, new OptionArgument<String>() {{
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
				maxValue(6);
				constraint(new Enumeration<Integer>() {{
					value(3, "for three");
					value(7, "for seven");
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
		
		//p.printCompleteHelp();
		//p.printShortHelp();
		//p.printGlobalOptionsHelp();
		//p.printCommandsHelp();
		//p.printCommandHelp("set");
		/*System.out.println();
		System.out.println("-----------------------------------");
		System.out.println();*/
		
		
		p.parsePrintAndExitOnError(
			new String[] { "-sMorop", "-sMiu", "--moi", "-d", "j", "set", "123", "3", "2", "level", "--number", "1000" }
			//new String[] { "--help", "cmd=set" }
			//new String[] { "--version" }
			
			// TODO: Create a test case for these.
			//new String[] { "-sMorop", "-sMiu", "--moi", "-d", "j", "set", "123", "3", "2", "level", "--numbe", "1000" }
			//new String[] { "-sMorop", "-sMiu", "--moi", "-d", "j", "set", "123", "3", "2", "level", "--number", "-10001" }
			//new String[] { "-sMorop", "-sMiu", "--moi", "-d", "j", "set", "123", "3", "2", "level", "--number"}
			//new String[] { "-sMorop", "-sMiu", "--moi", "-d", "j", "set", "123", "2", "level", "--number", "1000" }
			//new String[] { "-sMorop", "-sMiu", "--moi", "-d", "j", "set", "123", "2", "level", "--number" }
		);
		
		System.out.println(p.optionExists("-m"));
		System.out.println(p.optionExists("-a"));
		System.out.println(p.optionValue("-m"));
		System.out.println(p.optionValue("-n"));
	
		System.out.println("_miu = " + _miu);
		System.out.println("_a = " + _a);
		System.out.println("_numba = " + _numba[0]);
		System.out.println("cmd = " + _cmd);
		System.out.println("type = " + _type);
		System.out.println("Success.");
	}
}
