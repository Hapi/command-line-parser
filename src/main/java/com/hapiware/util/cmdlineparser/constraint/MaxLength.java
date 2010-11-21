package com.hapiware.util.cmdlineparser.constraint;

import com.hapiware.util.cmdlineparser.Description;


/**
 * {@code MaxLength} is used to add a maximum length constraint for {@link String} arguments.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class MaxLength
	implements
		Constraint<String>
{
	private final int _maxLength;
	
	/**
	 * Constructs a {@code MaxLength} constraint for a {@link String} argument.
	 * 
	 * @param maxLength
	 * 		A maximum length of {@link String}.
	 */
	public MaxLength(int maxLength)
	{
		_maxLength = maxLength;
	}
	
	public boolean typeCheck(Class<?> typeClass)
	{
		return typeClass == String.class;
	}
	
	public void evaluate(String argumentName, String value) throws ConstraintException
	{
		if(value.length() > _maxLength) {
			String str =
				"Length of '" + value + "' is longer than the maximum length "
					+ _maxLength + " allowed for '" + argumentName + "'."; 
			throw new ConstraintException(str);
		}
	}

	public Description description()
	{
		return new Description().description("Maximum length is " + _maxLength + " characters.");
	}

	/**
	 * Returns the maximum length as {@code String}.
	 */
	@Override
	public String toString()
	{
		return String.valueOf(_maxLength);
	}
}
