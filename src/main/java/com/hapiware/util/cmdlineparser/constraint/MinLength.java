package com.hapiware.util.cmdlineparser.constraint;

import com.hapiware.util.cmdlineparser.Description;

/**
 * {@code MinLength} is used to add a minimum length constraint for {@link String} arguments.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class MinLength
	implements
		Constraint<String>
{
	private final int _minLength;
	
	/**
	 * Constructs a {@code MinLength} constraint for a {@link String} argument.
	 * 
	 * @param minLength
	 * 		A minimum length of {@link String}.
	 */
	public MinLength(int minLength)
	{
		_minLength = minLength;
	}
	
	public boolean typeCheck(Class<?> typeClass)
	{
		return typeClass == String.class;
	}
	
	public void evaluate(String argumentName, String value) throws ConstraintException
	{
		if(value.length() < _minLength) {
			String str =
				"Length of '" + value + "' is shorter than the minimum length "
					+ _minLength + " allowed for '" + argumentName + "'."; 
			throw new ConstraintException(str);
		}
	}

	public Description description()
	{
		return new Description().description("Minimum length is " + _minLength + " characters.");
	}

	/**
	 * Returns the minimum length as {@code String}.
	 */
	@Override
	public String toString()
	{
		return String.valueOf(_minLength);
	}
}
