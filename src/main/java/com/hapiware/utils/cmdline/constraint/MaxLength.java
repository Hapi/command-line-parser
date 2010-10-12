package com.hapiware.utils.cmdline.constraint;


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
				"[" + value + "] is greater than the maximum length "
					+ _maxLength + " allowed for '" + argumentName + "'"; 
			throw new ConstraintException(str);
		}
	}
}
