package com.hapiware.utils.cmdline.constraint;


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
}
