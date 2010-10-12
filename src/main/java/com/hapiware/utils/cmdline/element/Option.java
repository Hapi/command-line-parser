package com.hapiware.utils.cmdline.element;

import java.util.List;
import java.util.Set;

import com.hapiware.utils.cmdline.constraint.ConstraintException;


public class Option
{
	private ElementBase _option = new ElementBase();
	private Argument.Inner<?> _definedArgument;
	private boolean _multiple;
	
	private <T> Option(Option option)
	{
		_option = new ElementBase(option._option);
		if(option._definedArgument != null)
			_definedArgument = option._definedArgument.clone();
		_multiple = option._multiple;
	}
	
	public Option()
	{
		// Does nothing.
	}
	
	public Option name(String name)
	{
		if(name == null || name.trim().length() == 0)
			throw new NullPointerException("'name' must have a value.");
		if(name.startsWith("-"))
			throw new IllegalArgumentException("'name' must not start with minus (-).");
		
		_option.name(addOptionMinus(name));
		return this;
	}
	
	public Option alternatives(String...alternatives)
	{
		if(alternatives == null || alternatives.length == 0)
			throw new NullPointerException("'alternatives' must have a value.");
		for(int i = 0; i < alternatives.length; i++)
			alternatives[i] = addOptionMinus(alternatives[i]);
		_option.alternatives(alternatives);
		return this;
	}
	
	public Option id(String id)
	{
		if(id == null || id.trim().length() == 0)
			throw new NullPointerException("'id' must have a value.");
		_option.id(id);
		return this;
	}
	
	public Option description(String description)
	{
		if(description == null || description.trim().length() == 0)
			throw new NullPointerException("'description' must have a value.");
		_option.description(description);
		return this;
	}
	public Option p()
	{
		_option.p();
		return this;
	}

	public <T> Option set(Class<T> argumentType, Argument argument)
	{
		if(argumentType == null)
			throw new NullPointerException("'argumentType' must have a value.");
		if(argument == null)
			throw new NullPointerException("'argument' must have a value.");
		
		argument.id(removeOptionMinusFromId(_option.id()));
		_definedArgument = new Argument.Inner<T>(argument, argumentType);
		if(_definedArgument.id() == null || _definedArgument.id().trim().length() == 0)
			throw new NullPointerException("'argument' must have an id.");
		return this;
	}
	
	public Option multiple()
	{
		_multiple = true;
		return this;
	}

	private static String addOptionMinus(String name)
	{
		return (name.length() > 1 ? "--" : "-") + name;
	}
	
	private static String removeOptionMinusFromId(String id)
	{
		if(id.startsWith("-"))
			return (id.length() > 2 ? id.substring(2) : id.substring(1));
		else
			return id;
	}
	
	
	public static class Inner
		implements
			Parser
	{
		private Option _outer;
		public Inner(Option outer)
		{
			_outer = outer;
		}
		public Inner(Inner inner)
		{
			_outer = new Option(inner._outer);
		}
		public String name()
		{
			return _outer._option.name();
		}
		public boolean checkAlternative(String name)
		{
			return _outer._option.checkAlternative(name);
		}
		public Set<String> alternatives()
		{
			return _outer._option.alternatives();
		}
		public String id()
		{
			return _outer._option.id();
		}
		public List<String> description()
		{
			return _outer._option.description();
		}
		@SuppressWarnings("unchecked")
		public <T> Argument.Inner<T> argument()
		{
			return (Argument.Inner<T>)_outer._definedArgument;
		}
		public boolean multiple()
		{
			return _outer._multiple;
		}
		public boolean parse(List<String> arguments)
			throws
				ConstraintException
		{
			if(arguments.size() == 0)
				return false;
			
			String optionName = (String)arguments.get(0);
			if(optionName.equals(name()) || checkAlternative(optionName)) {
				arguments.remove(0);
				if(argument() != null) {
					argument().name(optionName);
					return argument().parse(arguments);
				}
				else
					return true;
			}
			return false;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj == this)
				return true;

			if(!(obj instanceof Option.Inner))
				return false;
			Option.Inner inner = (Option.Inner)obj;
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
			str += "name: " + name() + ", id: " + id() + ", multi: " + multiple();
			if(alternatives().size() > 0) {
				str += ", alt: ";
				int i = 0;
				for(String alternative : alternatives())
					str += alternative + (i++ < alternatives().size() ? ", " : "");
			}
			str += "]";
			return str;
		}
	}
}
