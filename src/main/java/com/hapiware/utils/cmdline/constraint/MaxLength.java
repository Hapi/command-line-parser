package com.hapiware.utils.cmdline.constraint;

import com.hapiware.utils.cmdline.element.Description;


public class MaxLength
	implements
		Constraint
{
	private final int _maxLength;
	
	public MaxLength(int maxLength)
	{
		_maxLength = maxLength;
	}
	
	public void evaluate(String argumentName, Object value) throws ConstraintException
	{
		if(((String)value).length() > _maxLength) {
			String str =
				"Length of [" + value + "] is longer than the maximum length "
					+ _maxLength + " allowed for '" + argumentName + "'"; 
			throw new ConstraintException(str);
		}
	}

	public Description description()
	{
		return new Description().description("Maximum length is " + _maxLength + " characters.");
	}
}
