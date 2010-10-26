package com.hapiware.util.cmdlineparser.constraint;

import com.hapiware.util.cmdlineparser.Description;


public class Length
	implements
		Constraint<String>
{
	private final int _length;
	
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
