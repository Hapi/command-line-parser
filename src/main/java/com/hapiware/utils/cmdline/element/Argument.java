package com.hapiware.utils.cmdline.element;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.hapiware.utils.cmdline.Util;
import com.hapiware.utils.cmdline.constraint.ConfigurationException;
import com.hapiware.utils.cmdline.constraint.Constraint;
import com.hapiware.utils.cmdline.constraint.ConstraintException;
import com.hapiware.utils.cmdline.constraint.Enumeration;
import com.hapiware.utils.cmdline.constraint.IllegalCommandLineArgumentException;
import com.hapiware.utils.cmdline.constraint.MaxLength;
import com.hapiware.utils.cmdline.constraint.MaxValue;
import com.hapiware.utils.cmdline.constraint.MinLength;
import com.hapiware.utils.cmdline.constraint.MinValue;

public class Argument
{
	private ElementBase _argument = new ElementBase();
	private List<Constraint> _constraints = new LinkedList<Constraint>();
	private boolean _hasEnumConstraint = false;
	private boolean _optional;
	private String _defaultForOptional;
	
	private Argument(Argument argument)
	{
		_argument = new ElementBase(argument._argument);
		_optional = argument._optional;
		_defaultForOptional = argument._defaultForOptional;
		_constraints.addAll(argument._constraints);
	}
	
	protected Argument()
	{
		// Does nothing.
	}
	
	public Argument(String name)
	{
		if(name == null || name.trim().length() == 0)
			throw new NullPointerException("'name' must have a value.");
		
		_argument.name(name);
	}
	
	public Argument id(String id)
	{
		if(id == null || id.trim().length() == 0)
			throw new ConfigurationException("'id' for '" + _argument.name() + "' must have a value.");
		
		_argument.id(id);
		return this;
	}
	
	public Argument description(String description)
	{
		if(description == null || description.trim().length() == 0)
			throw
				new ConfigurationException(
					"'description' for '" + _argument.name() + "' must have a value."
				);
		
		_argument.description(description);
		return this;
	}
	public Argument p()
	{
		_argument.p();
		return this;
	}
	
	public Argument optional()
	{
		_optional = true;
		return this;
	}
	
	public <T> Argument optional(T defaultValue)
	{
		if(defaultValue == null || defaultValue.toString().length() == 0)
			throw
				new ConfigurationException(
					"'defaultValue' for '" + _argument.name() + "' must have a value."
				);
		
		_defaultForOptional = defaultValue.toString();
		_optional = true;
		return this;
	}
	
	public Argument constraint(Constraint constraint)
	{
		String forName = _argument.name() == null ? "" : " for " + _argument.name();
		if(constraint == null)
			throw new ConfigurationException("'constraint'" + forName + " must have a value.");
		if(_hasEnumConstraint)
			throw
				new ConfigurationException(
					"Only one Enumeration constraint can be defined" + forName + "."
				);
		if(constraint instanceof Enumeration)
			_hasEnumConstraint = true;
		if(constraint.description() == null || constraint.description().toParagraphs().size() == 0)
			throw new ConfigurationException("A missing constraint description" + forName + ".");
		
		_constraints.add(constraint);
		return this;
	}

	public Argument maxLength(int maxLength)
	{
		return constraint(new MaxLength(maxLength));
	}
	
	public Argument minLength(int minLength)
	{
		return constraint(new MinLength(minLength));
	}
	
	public Argument minValue(Number minValue)
	{
		return constraint(new MinValue(minValue));
	}
	
	public Argument maxValue(Number maxValue)
	{
		return constraint(new MaxValue(maxValue));
	}
	
	
	public static final class Inner<T>
		implements
			Parser,
			Cloneable
	{
		private Argument _outer;
		private final Class<T> _argumentTypeClass;
		private T _value;
		
		public Inner(Argument outer, Class<T> argumentTypeClass)
		{
			_outer = outer;
			_argumentTypeClass = argumentTypeClass;
		}
		public String name()
		{
			return _outer._argument.name();
		}
		public void name(String name)
		{
			_outer._argument.name(name);
		}
		public String id()
		{
			return _outer._argument.id();
		}
		public List<String> description()
		{
			return _outer._argument.description();
		}
		public void value(T value)
		{
			_value = value;
		}
		public T value()
		{
			return _value;
		}
		public List<Constraint> constraints()
		{
			return Collections.unmodifiableList(_outer._constraints);
		}
		public boolean optional()
		{
			return _outer._optional;
		}
		public boolean hasDefaultValueForOptional()
		{
			return _outer._defaultForOptional != null;
		}
		public String defaultValueAsString()
		{
			return _outer._defaultForOptional;
		}
		public String defaultValueDescription()
		{
			if(hasDefaultValueForOptional())
				return 
					"Default value for the optional argument is " 
						+ _outer._defaultForOptional 
						+ ". ";
			else
				return "";
		}
		public boolean parse(List<String> arguments)
			throws
				ConstraintException,
				IllegalCommandLineArgumentException
		{
			boolean defaultValueAdded = false;
			if(arguments.size() == 0)
				if(optional()) {
					if(defaultValue() != null) {
						((LinkedList<String>)arguments).addFirst(defaultValue().toString());
						defaultValueAdded = true;
					}
					else
						return true;
				}
				else
					return false;
			
			try {
				value(_argumentTypeClass.cast(valueOf(arguments.get(0), _argumentTypeClass)));
			}
			catch(IllegalCommandLineArgumentException e) {
				if(defaultValueAdded)
					throw e;
				else {
					if(defaultValue() != null) {
						((LinkedList<String>)arguments).addFirst(defaultValue().toString());
						value(_argumentTypeClass.cast(valueOf(arguments.get(0), _argumentTypeClass)));
					}
					else
						return true;
				}
			}
			arguments.remove(0);
			return true;
		}
		public void checkConstraints() throws ConstraintException
		{
			for(Constraint constraint : constraints())
				constraint.evaluate(name(), value());
		}
		
		@Override
		public Inner<T> clone()
		{
			Inner<T> inner = new Inner<T>(new Argument(_outer), _argumentTypeClass);
			inner._value = _value;
			return inner;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj == this)
				return true;

			if(!(obj instanceof Argument.Inner<?>))
				return false;
			Argument.Inner<?> inner = (Argument.Inner<?>)obj;
			return name().equals(inner.name());
		}

		@Override
		public int hashCode()
		{
			int resultHash = 17;
			resultHash = 31 * resultHash + (name() == null ? 0 : name().hashCode());
			return resultHash;
		}
		
		@Override
		public String toString()
		{
			String str = "[";
			str += "name: " + name() + ", id: " + id() + ", optional: " + optional() + "]";
			return str;
		}
		
		private T defaultValue() throws IllegalCommandLineArgumentException
		{
			return _argumentTypeClass.cast(valueOf(_outer._defaultForOptional, _argumentTypeClass));
		}
		private Object valueOf(String valueAsString, Class<?> argumentTypeClass)
			throws
				IllegalCommandLineArgumentException
		{
			try {
				return Util.valueOf(valueAsString, argumentTypeClass);
			}
			catch(NumberFormatException ex) {
				String msg =
					"[" + valueAsString + "] cannot be interpreted as "
						+ argumentTypeClass.getCanonicalName()
						+ " for '" + name() + "'";
				throw new IllegalCommandLineArgumentException(msg, ex);
			}
		}
	}
}
