package com.hapiware.utils.cmdline.constraint;

import com.hapiware.utils.cmdline.element.Description;


public class MinLength
	implements
		Constraint
{
	private final int _minLength;
	
	public MinLength(int minLength)
	{
		_minLength = minLength;
	}
	
	public void evaluate(String argumentName, Object value) throws ConstraintException
	{
		if(((String)value).length() < _minLength) {
			String str =
				"[" + value + "] is smaller than the minimum length "
					+ _minLength + " allowed for '" + argumentName + "'"; 
			throw new ConstraintException(str);
		}
	}

	public Description description()
	{
		return new Description().description("Minimum length is " + _minLength + " characters.");
	}
}
