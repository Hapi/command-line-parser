package com.hapiware.util.cmdlineparser;

import java.lang.reflect.Field;


public class TestUtil
{
	public static void replaceExitHandler(CommandLineParser parser)
	{
		try {
			Field exitHandler = parser.getClass().getDeclaredField("_exitHandler");
			exitHandler.setAccessible(true);
			exitHandler.set(
				parser,
				new ExitHandler()
				{
					public void exit(int status)
					{
						throw new ExitException(status);
					}
				}
			);
		}
		catch(Throwable e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
