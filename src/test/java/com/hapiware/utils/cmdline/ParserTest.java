package com.hapiware.utils.cmdline;

import com.hapiware.utils.cmdline.annotation.Id;
import com.hapiware.utils.cmdline.constraint.Enumeration;
import com.hapiware.utils.cmdline.element.Argument;
import com.hapiware.utils.cmdline.element.Command;
import com.hapiware.utils.cmdline.element.Option;

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
		CommandLineParser p = new CommandLineParser();
		p.add(new Option() {{
			name("m").alternatives("moi").id("miu");
			description("dfšlasjdfa lsfj");
		}});
		p.add(new Option() {{
			name("n").alternatives("number");
			set(Integer.class, new Argument() {{
				optional(5);
				minValue(1);
				maxValue(1000);
			}});
		}});
		p.add(new Option() {{
			name("s").multiple();
			set(String.class, new Argument() {{
				maxLength(5);
			}});
		}});
		p.add(new Option() {{
			name("d");
			set(String.class, new Argument() {{
				//maxLength(5);
				constraint(new Enumeration() {{
					value("J").ignoreCase();
					value("4");
				}});
			}});
		}});
		
		p.add(new Command() {{
			name("set").alternatives("s").id("cmd");
			add(Integer.class, new Argument() {{
				name("PID");
				description("Process ID for JVM.");
			}});
			add(Integer.class, new Argument() {{
				optional(4);
				name("TYPE");
				//maxLength(5);
				minValue(3);
				maxValue(6);
				/*constraint(new Enumeration() {{
					value("J").ignoreCase().description("For Java loggers.");
					value("4").description("For log4j loggers.");
				}});*/
				description("....");
			}});
			add(Integer.class, new Argument() {{
				name("RE");
				description("Regular expression.");
			}});
			add(String.class, new Argument() {{
				name("LEVEL");
				description("New logging level.");
			}});
			add(new Option() {{
				name("v");
			}});
		}});
		
		
		
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
