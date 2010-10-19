package com.hapiware.util.cmdlineparser.constraint;

import com.hapiware.util.cmdlineparser.ConfigurationException;
import com.hapiware.util.cmdlineparser.Description;


public class MaxValue<T extends Comparable<T>>
	implements
		Constraint<T>
{
	private final T _maxValue;
	
	public MaxValue(T maxValue)
	{
		if(maxValue == null)
			throw new ConfigurationException("'maxValue' must have a value.");
		_maxValue = maxValue;
	}
	
	public boolean typeCheck(Class<?> typeClass)
	{
		for(Class<?> i : typeClass.getInterfaces())
			if(i == Comparable.class)
				return true;
		return false;
	}
	
	public void evaluate(String argumentName, T value) throws ConstraintException
	{
		if(_maxValue.compareTo(value) < 0) {
			String str =
				"[" + value + "] is greater than the maximum value "
					+ _maxValue + " allowed for '" + argumentName + "'"; 
			throw new ConstraintException(str);
		}
	}

	public Description description()
	{
		return new Description().description("Maximum value is " + _maxValue + ".");
	}
	
	/**
	 * Returns the maximum value as {@code String}.
	 */
	@Override
	public String toString()
	{
		return _maxValue.toString();
	}
}