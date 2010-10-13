package com.hapiware.utils.cmdline.constraint;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.hapiware.utils.cmdline.element.Description;


public class MinValue
	implements
		Constraint
{
	private final Number _minValue;
	
	public MinValue(Number minValue)
	{
		_minValue = minValue;
	}
	
	public void evaluate(String argumentName, Object value) throws ConstraintException
	{
		Number number = (Number)value;
		boolean isOk = false;
		if(_minValue instanceof Integer)
			isOk = number.intValue() >= _minValue.intValue();
		else if(_minValue instanceof Long)
			isOk = number.longValue() >= _minValue.longValue();
		else if(_minValue instanceof Byte)
			isOk = number.byteValue() >= _minValue.byteValue();
		else if(_minValue instanceof Short)
			isOk = number.shortValue() >= _minValue.shortValue();
		else if(_minValue instanceof Double)
			isOk = Double.valueOf(number.doubleValue()).compareTo(_minValue.doubleValue()) >= 0;
		else if(_minValue instanceof Float)
			isOk = Float.valueOf(number.floatValue()).compareTo(_minValue.floatValue()) >= 0;
		else if(_minValue instanceof BigInteger)
			isOk = ((BigInteger)number).compareTo((BigInteger)_minValue) >= 0;
		else if(_minValue instanceof BigDecimal)
			isOk = ((BigDecimal)number).compareTo((BigDecimal)_minValue) >= 0;
			
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
