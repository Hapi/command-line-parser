package com.hapiware.util.cmdlineparser.constraint;

import com.hapiware.util.cmdlineparser.Description;


/**
 * {@code Length} is used to add an exact length constraint for {@link String} arguments.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class Length
	implements
		Constraint<String>
{
	private final int _length;
	
	/**
	 * Constructs a {@code Length} constraint for a {@link String} argument.
	 * 
	 * @param length
	 * 		A length of {@link String}.
	 */
	public Length(int length)
	{
		_length = length;
	}
	
	public boolean typeCheck(Class<?> typeClass)
	{
		return typeClass == String.class;
	}
	
	public void evaluate(String argumentName, String value) throws ConstraintException
	{
		if(value.length() != _length) {
			String str =
				"Length of '" + value + "' differs from the length "
					+ _length + " allowed for '" + argumentName + "'."; 
			throw new ConstraintException(str);
		}
	}

	public Description description()
	{
		return new Description().description("Length is exactly " + _length + " characters.");
	}

	/**
	 * Returns the maximum length as {@code String}.
	 */
	@Override
	public String toString()
	{
		return String.valueOf(_length);
	}
}
