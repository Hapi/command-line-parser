package com.hapiware.utils.cmdline.constraint;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.hapiware.utils.cmdline.element.Description;


public class MaxValue
	implements
		Constraint
{
	private final Number _maxValue;
	
	public MaxValue(Number maxValue)
	{
		_maxValue = maxValue;
	}
	
	public void evaluate(String argumentName, Object value) throws ConstraintException
	{
		Number number = (Number)value;
		boolean isOk = false;
		if(_maxValue instanceof Integer)
			isOk = number.intValue() <= _maxValue.intValue();
		else if(_maxValue instanceof Long)
			isOk = number.longValue() <= _maxValue.longValue();
		else if(_maxValue instanceof Byte)
			isOk = number.byteValue() <= _maxValue.byteValue();
		else if(_maxValue instanceof Short)
			isOk = number.shortValue() <= _maxValue.shortValue();
		else if(_maxValue instanceof Double)
			isOk = Double.valueOf(number.doubleValue()).compareTo(_maxValue.doubleValue()) <= 0;
		else if(_maxValue instanceof Float)
			isOk = Float.valueOf(number.floatValue()).compareTo(_maxValue.floatValue()) <= 0;
		else if(_maxValue instanceof BigInteger)
			isOk = ((BigInteger)number).compareTo((BigInteger)_maxValue) <= 0;
		else if(_maxValue instanceof BigDecimal)
			isOk = ((BigDecimal)number).compareTo((BigDecimal)_maxValue) <= 0;
		
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
