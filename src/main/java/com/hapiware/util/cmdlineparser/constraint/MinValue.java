package com.hapiware.util.cmdlineparser.constraint;

import com.hapiware.util.cmdlineparser.ConfigurationException;
import com.hapiware.util.cmdlineparser.Description;


public class MinValue<T extends Comparable<T>>
	implements
		Constraint<T>
{
	private final T _minValue;
	
	public MinValue(T minValue)
	{
		if(minValue == null)
			throw new ConfigurationException("'minValue' must have a value.");
		_minValue = minValue;
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
		if(_minValue.compareTo(value) > 0) {
			String str =
				"'" + value + "' is smaller than the minimum value "
					+ _minValue + " allowed for '" + argumentName + "'."; 
			throw new ConstraintException(str);
		}
	}

	public Description description()
	{
		return
			new Description()
				.description("Minimum value is ")
				.strong(_minValue.toString())
				.description(".");
	}
	
	/**
	 * Returns the minimum value as {@code String}.
	 */
	@Override
	public String toString()
	{
		return _minValue.toString();
	}
}
