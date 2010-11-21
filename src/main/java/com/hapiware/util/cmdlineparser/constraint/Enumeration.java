package com.hapiware.util.cmdlineparser.constraint;

import java.util.LinkedList;
import java.util.List;

import com.hapiware.util.cmdlineparser.Argument;
import com.hapiware.util.cmdlineparser.ConfigurationException;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.OptionArgument;


/**
 * {@code Enumeration} constraint is used to set individual values and value ranges for
 * {@link Argument} and {@link OptionArgument} objects. {@code Enumeration} has setters
 * which can be chained thus allowing builder like usage.
 * <p>
 * Values are set with {@link #value(Object, String)} and {@link #valueIgnoreCase(Object, String)}
 * methods. Notice that {@link #valueIgnoreCase(Object, String)} can only be used for {@link String}
 * arguments (checked at runtime).
 * <p>
 * In addition to individual values value ranges can also be used. Value ranges can be set with
 * {@link #includeRange(Object, Object, String)} and {@link #excludeRange(Object, Object, String)}
 * methods.
 * <p>
 * The resolve order for include, exclude and value methods is:
 * 	<ol>
 * 		<li><b>include</b></li>
 * 		<li><b>exclude</b></li>
 * 		<li><b>value</b></li>
 * 	</ol>
 * 
 * This means that {@link #excludeRange(Object, Object, String)} overrides
 * {@link #includeRange(Object, Object, String)} but {@link #value(Object, String)} (and
 * {@link #valueIgnoreCase(Object, String)}) overrides excludes. For example:
 * <pre>
 * add(Integer.class, new Argument<Integer>("TYPE") {{
 *     constraint(new Enumeration<Integer>() {{
 *         value(5, "description for five");
 *         value(17, "description for seventeen");
 *         includeRange(1, 10, "description for 1 ... 10");
 *         excludeRange(3, 6, "description for 3 ... 6");
 *     }});
 *     description("Description for TYPE.");
 * }});
 * </pre>
 * all the following integers are valid {@code [1, 2, 5, 7, 8, 9, 10, 17]} while the others are
 * not.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @param <T>
 */
public class Enumeration<T>
	implements
		Constraint<T>
{
	private List<Enum<T>> _enumerations = new LinkedList<Enum<T>>();
	private List<Enum<T>> _includeRanges = new LinkedList<Enum<T>>();
	private List<Enum<T>> _excludeRanges = new LinkedList<Enum<T>>();
	
	/**
	 * Adds an individual value to the enumeration.
	 * 
	 * @param value
	 * 		A value to add.
	 * 
	 * @param description
	 * 		A description for the value.
	 * 
	 * @return
	 * 		An {@code Enumeration} object for chaining.
	 */
	public Enumeration<T> value(T value, String description)
	{
		_enumerations.add(new NormalEnum<T>(value, description));
		return this;
	}
	
	/**
	 * Adds a {@link String} value to the enumeration. Case is ignored. Notice that the type
	 * of the value is checked at runtime.  
	 * 
	 * @param value
	 * 		A value to add. Must be {@link String}. Type is checked at runtime.
	 * 
	 * @param description
	 * 		A description for the value.
	 * 
	 * @return
	 * 		An {@code Enumeration} object for chaining.
	 */
	@SuppressWarnings("unchecked")
	public Enumeration<T> valueIgnoreCase(T value, String description)
	{
		_enumerations.add((Enum<T>)new IgnoreCaseEnum((String) value, description));
		return this;
	}
	
	/**
	 * Adds an include range to the enumeration. Type of the arguments must implement
	 * {@link Comparable} interface (checked at runtime).
	 * 
	 * @param lower
	 * 		Lower limit of the include range.
	 * 
	 * @param upper
	 * 		Upper limit of the include range.
	 * 
	 * @param description
	 * 		A description for the include range.
	 * 
	 * @return
	 * 		An {@code Enumeration} object for chaining.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enumeration<T> includeRange(T lower, T upper, String description)
	{
		_includeRanges.add(new Range((Comparable<?>)lower, (Comparable<?>)upper, description));
		return this;
	}

	/**
	 * Adds an exclude range to the enumeration. Type of the arguments must implement
	 * {@link Comparable} interface (checked at runtime).
	 * 
	 * @param lower
	 * 		Lower limit of the exclude range.
	 * 
	 * @param upper
	 * 		Upper limit of the exclude range.
	 * 
	 * @param description
	 * 		A description for the exclude range.
	 * 
	 * @return
	 * 		An {@code Enumeration} object for chaining.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enumeration<T> excludeRange(T lower, T upper, String description)
	{
		_excludeRanges.add(new Range((Comparable<?>)lower, (Comparable<?>)upper, description));
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
				"Value for '" + argumentName + "' was '" + value + "' but it must be"
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
	
	
	/**
	 * Returns a {@code String} representation of {@code Enumeration} object. The form is:<p>
	 * 
	 * <code>{includes: INCLUDERANGES excludes: EXCLUDERANGES values: OTHERVALUES}</code> <p>
	 * 
	 * where: <p>
	 * 	<ul>
	 * 		<li>INCLUDERANGES is a list of possible range of valid values.</li>
	 * 		<li>EXCLUDERANGES is a list of possible range of invalid values.</li>
	 * 		<li>OTHERVALUES is a list of possible individual valid values.</li>
	 * 	<ul>
	 */
	@Override
	public String toString()
	{
		return
			"{includes:" + _includeRanges
				+ " excludes:" + _excludeRanges
				+ " values:" + _enumerations + "}";
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
				throw new ConfigurationException("'value' must have a value.");
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
				throw new ConfigurationException("'value' must have a value.");
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
				throw new ConfigurationException("'lower' must have a value.");
			if(upper == null)
				throw new ConfigurationException("'upper' must have a value.");
			if(lower.compareTo(upper) > 0)
				throw new IllegalArgumentException("'lower' must be lower than 'upper'.");
			if(!isComparable(lower.getClass()))
				throw new IllegalArgumentException("'lower' must be Comparable.");
			if(!isComparable(upper.getClass()))
				throw new IllegalArgumentException("'upper' must be Comparable.");
			_lower = lower;
			_upper = upper;
		}
		
		@Override
		public boolean typeCheck(Class<?> typeClass)
		{
			return isComparable(typeClass);
		}

		@Override
		public boolean evaluate(T value)
		{
			return _lower.compareTo(value) <= 0 && _upper.compareTo(value) >= 0;
		}

		@Override
		public String toString()
		{
			return "(" + _lower.toString() + " ... " + _upper.toString() + ")";
		}
		
		private boolean isComparable(Class<?> typeClass)
		{
			for(Class<?> i : typeClass.getInterfaces())
				if(i == Comparable.class)
					return true;
			return false;
		}
	}
}
