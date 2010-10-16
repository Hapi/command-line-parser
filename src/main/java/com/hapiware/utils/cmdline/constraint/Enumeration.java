package com.hapiware.utils.cmdline.constraint;

import java.util.LinkedList;
import java.util.List;

import com.hapiware.utils.cmdline.element.Description;


public class Enumeration<T>
	implements
		Constraint<T>
{
	private List<Enum<?>> _enumerations = new LinkedList<Enum<?>>();
	
	public Enumeration<T> value(T value, String description)
	{
		value(value, description, false);
		return this;
	}
	
	public Enumeration<T> valueIgnoreCase(T value, String description)
	{
		value(value, description, true);
		return this;
	}
	
	private void value(T value, String description, boolean ignoreCase)
	{
		_enumerations.add(new Enum<T>(value, description, ignoreCase));
	}

	public boolean typeCheck(Class<?> typeClass)
	{
		if(_enumerations.size() == 0)
			return false;
		return typeClass == _enumerations.get(0).value().getClass();
	}
	
	public void evaluate(String argumentName, Object value) throws ConstraintException
	{
		String str = value.toString();
		boolean isOk = false;
		for(Enum<?> e : _enumerations) {
			if(e._ignoreCase) {
				if(e.value().toString().equalsIgnoreCase(str)) {
					isOk = true;
					break;
				}
			}
			else {
				if(e.value().toString().equals(str)) {
					isOk = true;
					break;
				}
			}
		}
		
		if(!isOk) {
			String msg =
				"Value for '" + argumentName + "' was [" + value + "] but it must be"
					+ " one of these: " + _enumerations.toString();
			throw new ConstraintException(msg);
		}
	}


	public Description description()
	{
		Description description = new Description();
		for(Enum<?> e : _enumerations) {
			if(e._description == null || e._description.trim().length() == 0)
				return null;
			description.strong(e.value().toString()).description(", " + e._description).p();
		}
		return description;
	}
	
	private static class Enum<T> {
		private final T _value;
		private final String _description;
		private final boolean _ignoreCase;
		
		public Enum(T value, String description, boolean ignoreCase)
		{
			if(value == null)
				throw new NullPointerException("'value' must have a value.");
			if(description == null || description.trim().length() == 0)
				throw new ConfigurationException("'description' must have a value.");
			_value = value;
			_description = description;
			_ignoreCase = ignoreCase;
		}
		
		private T value()
		{
			return _value;
		}
		
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj == this)
				return true;

			if(!(obj instanceof Enumeration.Enum<?>))
				return false;
			Enumeration.Enum<?> object = (Enumeration.Enum<?>)obj;
			
			return _value.equals(object._value) && _ignoreCase == object._ignoreCase;
		}

		@Override
		public int hashCode()
		{
			int resultHash = 17;
			resultHash = 31 * resultHash + (_ignoreCase ? 1 : 0);
			resultHash = 31 * resultHash + (_value == null ? 0 : _value.hashCode());
			return resultHash;
		}
		
		@Override
		public String toString()
		{
			return _value.toString();
		}
	}
}
