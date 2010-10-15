package com.hapiware.utils.cmdline.constraint;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.hapiware.utils.cmdline.element.Description;


public class MinValue<T extends Number>
	implements
		Constraint<T>
{
	private final Number _minValue;
	
	public MinValue(Number minValue)
	{
		_minValue = minValue;
	}
	
	public boolean typeCheck(Class<?> typeClass)
	{
		return typeClass.getSuperclass() == Number.class;
	}
	
	public void evaluate(String argumentName, Number value) throws ConstraintException
	{
		boolean isOk = false;
		if(_minValue instanceof Integer)
			isOk = value.intValue() >= _minValue.intValue();
		else if(_minValue instanceof Long)
			isOk = value.longValue() >= _minValue.longValue();
		else if(_minValue instanceof Byte)
			isOk = value.byteValue() >= _minValue.byteValue();
		else if(_minValue instanceof Short)
			isOk = value.shortValue() >= _minValue.shortValue();
		else if(_minValue instanceof Double)
			isOk = Double.valueOf(value.doubleValue()).compareTo(_minValue.doubleValue()) >= 0;
		else if(_minValue instanceof Float)
			isOk = Float.valueOf(value.floatValue()).compareTo(_minValue.floatValue()) >= 0;
		else if(_minValue instanceof BigInteger)
			isOk = ((BigInteger)value).compareTo((BigInteger)_minValue) >= 0;
		else if(_minValue instanceof BigDecimal)
			isOk = ((BigDecimal)value).compareTo((BigDecimal)_minValue) >= 0;
			
		if(!isOk) {
			String str =
				"[" + value + "] is smaller than the minimum value "
					+ _minValue + " allowed for '" + argumentName + "'"; 
			throw new ConstraintException(str);
		}
	}

	public Description description()
	{
		return new Description().description("Minimum value is " + _minValue);
	}
}
