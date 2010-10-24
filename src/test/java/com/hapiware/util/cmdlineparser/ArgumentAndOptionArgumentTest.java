package com.hapiware.util.cmdlineparser;

import static org.testng.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.testng.annotations.Test;

/**
 * Checks that both {@link Argument} and {@link OptionArgument} has the same public methods.
 * Checks also that both classes return their own class type. This is important for chaining.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class ArgumentAndOptionArgumentTest
{
	@Test
	public void testCommonInterfaceBasedOnArgument()
		throws
			SecurityException
	{
		for(Method m : Argument.class.getDeclaredMethods()) {
			if(!Modifier.isPublic(m.getModifiers()))
				continue;
			String name = m.getName();
			Class<?> paramTypes[] = m.getParameterTypes();
			try {
				Method optionArgumentMethod =
					OptionArgument.class.getDeclaredMethod(name, paramTypes);
				if(optionArgumentMethod.getReturnType() != OptionArgument.class)
					fail(
						"Method '" + name + "' found but the return type was wrong. "
							+ "Must be OptionArgument<T>."
					);
			}
			catch(NoSuchMethodException e) {
				fail("OptionArgument<T> method '" + e.getMessage() + "' is missing.");
			}
		}
	}
	
	@Test
	public void testCommonInterfaceBasedOnOptionArgument()
		throws
			SecurityException
	{
		for(Method m : OptionArgument.class.getDeclaredMethods()) {
			if(!Modifier.isPublic(m.getModifiers()))
				continue;
			String name = m.getName();
			Class<?> paramTypes[] = m.getParameterTypes();
			try {
				Method argumentMethod =
					Argument.class.getDeclaredMethod(name, paramTypes);
				if(argumentMethod.getReturnType() != Argument.class)
					fail(
						"Method '" + name + "' found but the return type was wrong. "
							+ "Must be Argument<T>."
					);
			}
			catch(NoSuchMethodException e) {
				fail("Argument<T> method '" + e.getMessage() + "' is missing.");
			}
		}
	}
}
