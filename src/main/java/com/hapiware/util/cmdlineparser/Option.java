package com.hapiware.util.cmdlineparser;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.hapiware.util.cmdlineparser.constraint.ConstraintException;


/**
 * {@code Option} is an optional command line argument which is identified either by preceding
 * minus (-) or minus-minus (--). Minus (-) is for a short option and minus-minus (--) is for
 * long option.
 * <p>
 * Short option is always only a single letter and if defined to have {@link Argument} then the
 * short option letter and an argument can be written with or without a whitespace. For example
 * the following options are equivalent: {@code -Ttype} and {@code -T type}. Different short options
 * cannot be combined together. For example, let's assume options {@code -a} and {@code -b} has been
 * defined. The combination {@code -ab} is interpreted as {@code -a} having an argument {@code b}
 * <u>not as {@code -a} and {@code -b}</u>.
 * <p>
 * Long options always have at least two letters and their possible arguments must be separated
 * with a whitespace.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class Option
{
	private ElementBase _option = new ElementBase();
	private Argument.Internal<?> _definedArgument;
	private boolean _multiple;
	
	private Option(Option option)
	{
		_option = new ElementBase(option._option);
		if(option._definedArgument != null)
			_definedArgument = option._definedArgument.clone();
		_multiple = option._multiple;
	}
	
	/**
	 * Creates new {@code Option}.
	 * 
	 * @param name
	 * 		Name for the option. If the name is only a single letter then it is interpreted as
	 * 		a short option (i.e. starts with -). Otherwise it is interpreted as a long option
	 * 		(i.e. starts with --). Notice that the name must not have preceding minus(es).
	 * 
	 * @throws ConfigurationException
	 * 		If {@code name} is incorrectly formed.
	 */
	public Option(String name)
	{
		if(name == null || name.trim().length() == 0)
			throw new ConfigurationException("'name' must have a value.");
		if(name.startsWith("-"))
			throw
				new ConfigurationException(
					"'name' for option '" + name + "' must not start with minus (-)."
				);
		if(!Util.checkName(name))
			throw
				new ConfigurationException(
					"'name' for option is incorrect ('" + name + "')."
				);
		
		_option.name(addOptionMinus(name));
	}
	
	/**
	 * Defines alternative names for the {@link Option}.
	 * 
	 * @param alternatives
	 * 		An array of alternative names. If the name is only a single letter then it is
	 * 		interpreted as a short option (i.e. starts with -). Otherwise it is interpreted
	 * 		as a long option (i.e. starts with --). Notice that the name must not have preceding
	 * 		minus(es).
	 * 
	 * @return
	 * 		The option object for chaining.
	 */
	public Option alternatives(String...alternatives)
	{
		if(alternatives == null || alternatives.length == 0)
			throw
				new ConfigurationException(
					"'alternatives' for option '" + _option.name() + "' must have a value."
				);
		
		for(int i = 0; i < alternatives.length; i++) {
			if(!Util.checkName(alternatives[i]))
				throw
					new ConfigurationException(
						"Alternative name for option '" + _option.name() 
							+ "' is incorrect ('" + alternatives[i] + "')."
					);
			alternatives[i] = addOptionMinus(alternatives[i]);
		}
		String duplicateAlternative = _option.alternatives(alternatives);
		if(duplicateAlternative != null)
			throw
				new ConfigurationException(
					"All the alternative names for option '" + _option.name() + "' must be unique. "
					+ "Conflicting alternative name is '" + duplicateAlternative + "'." 
				);
		return this;
	}
	
	
	/**
	 * An optional {@code id} for annotation matching. If not defined the name given in
	 * {@link Option#Option(String)} is used as the id. For more information see
	 * <a href="CommandLineParser.html#cmdlineparser-annotations">CommandLineParser, chapter Annotations</a>.
	 * 
	 * @param id
	 * 		An id for the option.
	 * 
	 * @return
	 * 		The option object for chaining.
	 */
	public Option id(String id)
	{
		if(id == null || id.trim().length() == 0)
			throw
				new ConfigurationException(
					"'id' for option '" + _option.name() + "' must have a value."
				);
		if(!Util.checkName(id))
			throw
				new ConfigurationException(
					"'id' for option '" + _option.name() + "' is incorrect ('" + id + "')."
				);
		
		_option.id(id);
		return this;
	}
	
	/**
	 * For further details see {@link Description#description(String)}
	 */
	public Option description(String description)
	{
		if(description == null || description.trim().length() == 0)
			throw
				new ConfigurationException(
					"'description' for option '" + _option.name() + "' must have a value."
				);
		
		_option.description(description);
		return this;
	}
	
	/**
	 * For further details see {@link Description#strong(String)}
	 */
	public Option strong(String text)
	{
		_option.strong(text);
		return this;
	}
	
	/**
	 * For further details see {@link Description#paragraph()}
	 */
	public Option paragraph()
	{
		_option.paragraph();
		return this;
	}
	
	/**
	 * For further details see {@link Description#d(String)}
	 */
	public Option d(String description)
	{
		description(description);
		return this;
	}
	
	/**
	 * For further details see {@link Description#b(String)}
	 */
	public Option b(String text)
	{
		strong(text);
		return this;
	}
	
	/**
	 * For further details see {@link Description#p()}
	 */
	public Option p()
	{
		paragraph();
		return this;
	}
	
	/**
	 * Set the argument for the option.
	 * 
	 * @param <T>
	 * 		Argument type (for compile time checking).
	 * 
	 * @param argumentType
	 * 		Argument type for run time checking.
	 * 
	 * @param argument
	 * 		The argument definition.
	 * 
	 * @return
	 * 		The option object for chaining.
	 */
	public <T> Option set(Class<T> argumentType, OptionArgument<T> argument)
	{
		if(argumentType == null)
			throw new ConfigurationException("'argumentType' must have a value.");
		if(argument == null)
			throw new ConfigurationException("'argument' must have a value.");
		
		argument.setId(removeOptionMinusFromId(_option.id()));
		_definedArgument = new Argument.Internal<T>(argument, argumentType);
		if(_definedArgument.id() == null || _definedArgument.id().trim().length() == 0)
			throw 
				new ConfigurationException(
					"'argument' for option '" + _option.name() + "' must have an id."
				);
		
		if(_definedArgument.description().size() > 0)
			throw
				new ConfigurationException(
					"Only the option '" + _option.name() + "' can have a description. "
						+ "'argument' must not have a description."
				);
		
		return this;
	}
	
	/**
	 * Allows multiple occurrences of the option in the command line.
	 * 
	 * @return
	 * 		The option object for chaining.
	 */
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
		return Util.removeOptionMinusFromId(id);
	}
	

	/**
	 * {@code Option.Data} is a container class for holding information about the given options. <p>
	 * 
	 * This class is immutable <b>only if {@link Argument.Data} is immutable</b>.
	 * 
	 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
	 *
	 */
	public static final class Data
		extends
			DataBase
	{
		private final Argument.Data<?> _argument;
		private final boolean _allowMultipleOccurences;
		
		/**
		 * "Copy" constructs a data object from the internal option object.
		 * 
		 * @param internal
		 * 		The internal option object.
		 */
		Data(Internal internal)
		{
			super(internal.name(), internal.id(), internal.alternatives());
			if(internal.argument() != null)
				_argument =  internal.argument().createDataObject();
			else
				_argument = null;
			_allowMultipleOccurences = internal.multiple();
		}
		
		public Set<String> getAlternatives()
		{
			return super.getAlternatives();
		}

		/**
		 * Returns the argument of the given option. {@code null} if the option definition does
		 * not have an argument.
		 * 
		 * @return
		 * 		The option argument. {@code null} if the option definition does not have
		 * 		an argument.
		 */
		public Argument.Data<?> getArgument()
		{
			return _argument;
		}

		/**
		 * Tells if the option can occur multiple times.
		 * 
		 * @return
		 * 		{@code true} if the option can occur multiple times within a single command line
		 * 		command. {@code false} otherwise.
		 */
		public boolean allowMultipleOccurences()
		{
			return _allowMultipleOccurences;
		}
		
		/**
		 * Returns a {@code String} representation of {@code Option.Data} object. The form is:
		 * <p>
		 * <code>{NAME(ID, MULTI) = VALUE (OPTIONAL) : ALTERNATIVES}</code>
		 * <p>
		 * where:
		 * 	<ul>
		 * 		<li>NAME is the argument name.</li>
		 * 		<li>ID is the id for the argument.</li>
		 * 		<li>MULTI indicates if the option can occur multiple times.</li>
		 * 		<li>VALUE is the argument value (if the value has been defined).</li>
		 * 		<li>OPTIONAL indicates if the value is optional or not.</li>
		 * 		<li>ALTERNATIVES alternative names for the option</li>
		 * 	</ul>
		 */
		@Override
		public String toString()
		{
			String str =
				"{" + getName() + "(" + getId() + ", " + allowMultipleOccurences() + ")"
					+ (
						getArgument() != null ? 
							" = " + getArgument().getValue() + "(" + getArgument().isOptional() + ")"
							: ""
					);
			str += " : " + getAlternatives();
			str += "}";
			return str;
		}
	}
	

	static final class Internal
		implements
			Parser
	{
		private Option _outer;
		
		public Internal(Option outer)
		{
			_outer = outer;
		}
		public Internal(Internal internal)
		{
			_outer = new Option(internal._outer);
		}
		public String name()
		{
			return _outer._option.name();
		}
		private boolean checkAlternative(String name)
		{
			return _outer._option.checkAlternative(name);
		}
		public Set<String> alternatives()
		{
			return Collections.unmodifiableSet(_outer._option.alternatives());
		}
		public String id()
		{
			return _outer._option.id();
		}
		public List<String> description()
		{
			return _outer._option.description();
		}
		public Argument.Internal<?> argument()
		{
			return _outer._definedArgument;
		}
		public boolean multiple()
		{
			return _outer._multiple;
		}
		public boolean parse(List<String> arguments)
			throws
				ConstraintException,
				IllegalCommandLineArgumentException
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

			if(!(obj instanceof Option.Internal))
				return false;
			Option.Internal internal = (Option.Internal)obj;
			return name().equals(internal.name());
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
			String str =
				"{" + name() + "(" + id() + ", " + multiple() + ")"
					+ (argument() != null ? " = " + argument().value() : "");
			str += " : " + alternatives();
			str += "}";
			return str;
		}
	}
}
