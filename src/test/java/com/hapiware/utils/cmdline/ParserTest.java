package com.hapiware.utils.cmdline;

import com.hapiware.utils.cmdline.annotation.Id;
import com.hapiware.utils.cmdline.constraint.Enumeration;
import com.hapiware.utils.cmdline.element.Argument;
import com.hapiware.utils.cmdline.element.Command;
import com.hapiware.utils.cmdline.element.Description;
import com.hapiware.utils.cmdline.element.Option;
import com.hapiware.utils.cmdline.element.OptionArgument;
import com.hapiware.utils.cmdline.writer.ScreenWriter;

public class ParserTest
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
			alternatives("number");
			description("Description");
			set(Integer.class, new OptionArgument() {{
				optional(5);
				minValue(1);
				maxValue(1000);
			}});
		}});
		p.add(new Option("s") {{
			multiple();
			description("Description");
			set(String.class, new OptionArgument() {{
				maxLength(5);
			}});
		}});
		p.add(new Option("d") {{
			description("Description");
			set(String.class, new OptionArgument() {{
				//maxLength(5);
				constraint(new Enumeration() {{
					value("J").ignoreCase().description("For Java loggers.");
					value("4").description("For log4j loggers.");
				}});
			}});
		}});
		
		p.add(new Command("set") {{
			alternatives("s").id("cmd");
			description("Description");
			shortDescription("Desc.");
			add(Integer.class, new Argument("PID") {{
				description("Process ID for JVM.");
			}});
			add(Integer.class, new Argument("TYPE") {{
				optional(4);
				//maxLength(5);
				minValue(3);
				maxValue(6);
				/*constraint(new Enumeration() {{
					value("J").ignoreCase().description("For Java loggers.");
					value("4").description("For log4j loggers.");
				}});*/
				description("....");
			}});
			add(Integer.class, new Argument("RE") {{
				description("Regular expression.");
			}});
			add(String.class, new Argument("LEVEL") {{
				description("New logging level.");
			}});
			add(new Option("v") {{
				description("Description");
			}});
		}});
		
		p.printHelp(new ScreenWriter(80));
		
		
		p.parsePrintAndExitOnError(
			new String[] { "-sMorop", "-sMiu", "--moi", "-d", "j", "set", "123", "3", "2", "level", "--number", "1000" }
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
