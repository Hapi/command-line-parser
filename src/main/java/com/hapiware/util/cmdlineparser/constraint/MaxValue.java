package com.hapiware.util.cmdlineparser.constraint;

import com.hapiware.util.cmdlineparser.ConfigurationException;
import com.hapiware.util.cmdlineparser.Description;


/**
 * {@code MaxValue} is used to create a maximum value constraint for arguments.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @param <T>
 * 		Type parameter must be {@link Comparable}.
 */
public class MaxValue<T extends Comparable<T>>
	implements
		Constraint<T>
{
	private final T _maxValue;
	
	/**
	 * Constructs a {@code MaxValue} constraint.
	 * 
	 * @param maxValue
	 * 		A maximum value for the constraint.
	 */
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
				"'" + value + "' is greater than the maximum value "
					+ _maxValue + " allowed for '" + argumentName + "'."; 
			throw new ConstraintException(str);
		}
	}

	public Description description()
	{
		return
			new Description()
				.description("Maximum value is ")
				.strong(_maxValue.toString())
				.description(".");
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
