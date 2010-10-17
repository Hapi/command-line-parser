package com.hapiware.utils.cmdline.constraint;

import java.util.LinkedList;
import java.util.List;

import com.hapiware.utils.cmdline.element.Description;


public class Enumeration<T>
	implements
		Constraint<T>
{
	private List<Enum<T>> _enumerations = new LinkedList<Enum<T>>();
	private List<Enum<T>> _includeRanges = new LinkedList<Enum<T>>();
	private List<Enum<T>> _excludeRanges = new LinkedList<Enum<T>>();
	
	public Enumeration<T> value(T value, String description)
	{
		_enumerations.add(new NormalEnum<T>(value, description));
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public Enumeration<T> valueIgnoreCase(T value, String description)
	{
		_enumerations.add((Enum<T>)new IgnoreCaseEnum((String) value, description));
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public Enumeration<T> includeRange(T lower, T upper, String description)
	{
		_includeRanges.add(new Range((Comparable)lower, (Comparable)upper, description));
		return this;
	}

	@SuppressWarnings("unchecked")
	public Enumeration<T> excludeRange(T lower, T upper, String description)
	{
		_excludeRanges.add(new Range((Comparable)lower, (Comparable)upper, description));
		return this;
	}

	public boolean typeCheck(Class<?> typeClass)
	{
		for(Enum<T> e : _includeRanges)
			if(!e.typeCheck(typeClass))
				return false;
		for(Enum<T> e : _excludeRanges)
			if(!e.typeCheck(typeClass))
				return false;
		for(Enum<T> e : _enumerations)
			if(!e.typeCheck(typeClass))
				return false;
		
		return true;
	}
	
	public void evaluate(String argumentName, T value) throws ConstraintException
	{
		if(!evaluate(value)) {
			String msg =
				"Value for '" + argumentName + "' was [" + value + "] but it must be"
					+ " one of these:"
					+ (_includeRanges.size() > 0 ? " " + _includeRanges.toString() : "")
					+ (_excludeRanges.size() > 0 ? " !" + _excludeRanges.toString() : "")
					+ (_enumerations.size() > 0 ? " " + _enumerations.toString() : "")
					+ ".";
			throw new ConstraintException(msg);
		}
	}

	private boolean evaluate(T value)
	{
		// Single values overrule includes and excludes.
		for(Enum<T> e : _enumerations)
			if(e.evaluate(value))
				return true;
		
		// Excludes overrule includes.
		for(Enum<T> e : _excludeRanges)
			if(e.evaluate(value))
				return false;
		
		// Includes are overruled by excludes (and single values).
		for(Enum<T> e : _includeRanges)
			if(e.evaluate(value))
				return true;
		
		return false;
	}

	public Description description()
	{
		Description description = new Description();
		for(Enum<?> e : _includeRanges)
			description.strong(e.toString()).description(", " + e._description).p();
		for(Enum<?> e : _excludeRanges)
			description.strong("not " + e.toString()).description(", " + e._description).p();
		for(Enum<?> e : _enumerations)
			description.strong(e.toString()).description(", " + e._description).p();
		return description;
	}
	
	
	private static abstract class Enum<T>
	{
		private final String _description;
		
		protected Enum(String description)
		{
			if(description == null || description.trim().length() == 0)
				throw new ConfigurationException("'description' must have a value.");
			_description = description;
		}
		
		public abstract boolean typeCheck(Class<?> typeClass);
		public abstract boolean evaluate(T value);
		public abstract String toString();
	}
	
	private static class NormalEnum<T>
		extends Enum<T>
	{
		private final T _value;
		
		public NormalEnum(T value, String description)
		{
			super(description);
			if(value == null)
				throw new NullPointerException("'value' must have a value.");
			_value = value;
		}
		
		@Override
		public boolean typeCheck(Class<?> typeClass)
		{
			return typeClass == _value.getClass();
		}

		@Override
		public boolean evaluate(T value)
		{
			return _value.equals(value);
		}

		@Override
		public String toString()
		{
			return _value.toString();
		}
	}
	
	private static class IgnoreCaseEnum
		extends
			Enum<String>
	{
		private final String _value;
		
		public IgnoreCaseEnum(String value, String description)
		{
			super(description);
			if(value == null)
				throw new NullPointerException("'value' must have a value.");
			_value = value;
		}
		
		@Override
		public boolean typeCheck(Class<?> typeClass)
		{
			return typeClass == String.class;
		}

		@Override
		public boolean evaluate(String value)
		{
			return _value.equalsIgnoreCase(value);
		}

		@Override
		public String toString()
		{
			return _value;
		}
	}
	
	private static class Range<T extends Comparable<T>>
		extends
			Enum<T>
	{
		private final T _lower ;
		private final T _upper;
		
		public Range(T lower, T upper, String description)
		{
			super(description);
			if(lower == null)
				throw new NullPointerException("'lower' must have a value.");
			if(upper == null)
				throw new NullPointerException("'upper' must have a value.");
			if(lower.compareTo(upper) > 0)
				throw new IllegalArgumentException("'lower' must be lower than 'upper'.");
			_lower = lower;
			_upper = upper;
		}
		
		@Override
		public boolean typeCheck(Class<?> typeClass)
		{
			return typeClass == _lower.getClass();
		}

		@Override
		public boolean evaluate(T value)
		{
			return _lower.compareTo(value) > 0 && _upper.compareTo(value) < 0;
		}

		@Override
		public String toString()
		{
			return "(" + _lower.toString() + " ... " + _upper.toString() + ")";
		}
		
	}
}
