package com.hapiware.utils.cmdline.constraint;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.hapiware.utils.cmdline.element.Description;


public class MaxValue<T extends Number>
	implements
		Constraint<T>
{
	private final Number _maxValue;
	
	public MaxValue(Number maxValue)
	{
		_maxValue = maxValue;
	}
	
	public boolean typeCheck(Class<?> typeClass)
	{
		return typeClass.getSuperclass() == Number.class;
	}
	
	public void evaluate(String argumentName, Number value) throws ConstraintException
	{
		boolean isOk = false;
		if(_maxValue instanceof Integer)
			isOk = value.intValue() <= _maxValue.intValue();
		else if(_maxValue instanceof Long)
			isOk = value.longValue() <= _maxValue.longValue();
		else if(_maxValue instanceof Byte)
			isOk = value.byteValue() <= _maxValue.byteValue();
		else if(_maxValue instanceof Short)
			isOk = value.shortValue() <= _maxValue.shortValue();
		else if(_maxValue instanceof Double)
			isOk = Double.valueOf(value.doubleValue()).compareTo(_maxValue.doubleValue()) <= 0;
		else if(_maxValue instanceof Float)
			isOk = Float.valueOf(value.floatValue()).compareTo(_maxValue.floatValue()) <= 0;
		else if(_maxValue instanceof BigInteger)
			isOk = ((BigInteger)value).compareTo((BigInteger)_maxValue) <= 0;
		else if(_maxValue instanceof BigDecimal)
			isOk = ((BigDecimal)value).compareTo((BigDecimal)_maxValue) <= 0;
		
		if(!isOk) {
			String str =
				"[" + value + "] is greater than the maximum value "
					+ _maxValue + " allowed for '" + argumentName + "'"; 
			throw new ConstraintException(str);
		}
	}

	public Description description()
	{
		return new Description().description("Maximum value is " + _maxValue);
	}
}
