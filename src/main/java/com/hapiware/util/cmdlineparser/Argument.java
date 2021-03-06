package com.hapiware.util.cmdlineparser;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.hapiware.util.cmdlineparser.constraint.Constraint;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;
import com.hapiware.util.cmdlineparser.constraint.Enumeration;
import com.hapiware.util.cmdlineparser.constraint.Length;
import com.hapiware.util.cmdlineparser.constraint.MaxLength;
import com.hapiware.util.cmdlineparser.constraint.MaxValue;
import com.hapiware.util.cmdlineparser.constraint.MinLength;
import com.hapiware.util.cmdlineparser.constraint.MinValue;


/**
 * {@code Argument} is used to define an argument for the command line utility itself (see
 * {@link CommandLineParser#add(Class, Argument)}) or for utility commands (see
 * {@link Command#add(Class, Argument)} and {@link CommandLineParser#add(Command)})
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @param <T>
 * 		A type parameter for the argument.
 * 
 * @see CommandLineParser#add(Class, Argument)
 * @see Command#add(Class, Argument)
 * @see CommandLineParser#add(Command)
 */
public class Argument<T>
{
	private ElementBase _argument = new ElementBase();
	private List<Constraint<T>> _constraints = new LinkedList<Constraint<T>>();
	private boolean _hasEnumConstraint = false;
	private boolean _showDefaultValueDescription = true;
	private T _defaultForOptional = null;
	
	private Argument(Argument<T> argument)
	{
		_argument = new ElementBase(argument._argument);
		_defaultForOptional = argument._defaultForOptional;
		_showDefaultValueDescription = argument._showDefaultValueDescription;
		_constraints.addAll(argument._constraints);
	}
	
	protected Argument()
	{
		// Does nothing.
	}
	
	/**
	 * Creates new {@code Argument}.
	 * 
	 * @param name
	 * 		Name for the argument.
	 * 
	 * @throws ConfigurationException
	 * 		If {@code name} is incorrectly formed.
	 * 
	 * @see Util#checkName(String)
	 */
	public Argument(String name)
	{
		if(name == null || name.trim().length() == 0)
			throw new ConfigurationException("'name' must have a value.");
		if(!Util.checkName(name))
			throw
				new ConfigurationException(
					"'name' for argument is incorrect ('" + name + "')."
				);
		
		_argument.name(name);
	}
	
	/**
	 * An optional {@code id} for annotation matching. If not defined the name given in
	 * {@link Argument#Argument(String)} is used as the id. For more information see
	 * <a href="CommandLineParser.html#cmdlineparser-annotations">CommandLineParser, chapter Annotations</a>.
	 * 
	 * @param id
	 * 		An id for the argument.
	 * 
	 * @return
	 * 		The argument object for chaining.
	 */
	public Argument<T> id(String id)
	{
		if(id == null || id.trim().length() == 0)
			throw new ConfigurationException("'id' for '" + _argument.name() + "' must have a value.");
		if(!Util.checkName(id))
			throw
				new ConfigurationException(
					"'id' for argument '" + _argument.name() + "' is incorrect ('" + id + "')."
				);
		
		_argument.id(id);
		return this;
	}
	
	/**
	 * For further details see {@link Description#description(String)}
	 */
	public Argument<T> description(String description)
	{
		if(description == null || description.trim().length() == 0)
			throw
				new ConfigurationException(
					"'description' for '" + _argument.name() + "' must have a value."
				);
		
		_argument.description(description);
		return this;
	}
	
	/**
	 * For further details see {@link Description#strong(String)}
	 */
	public Argument<T> strong(String text)
	{
		_argument.strong(text);
		return this;
	}
	
	/**
	 * For further details see {@link Description#paragraph()}
	 */
	public Argument<T> paragraph()
	{
		_argument.paragraph();
		return this;
	}
	
	/**
	 * For further details see {@link Description#d(String)}
	 */
	public Argument<T> d(String description)
	{
		description(description);
		return this;
	}
	
	/**
	 * For further details see {@link Description#b(String)}
	 */
	public Argument<T> b(String text)
	{
		strong(text);
		return this;
	}
	
	/**
	 * For further details see {@link Description#p()}
	 */
	public Argument<T> p()
	{
		paragraph();
		return this;
	}
	
	
	/**
	 * Sets the argument to be an optional one. Calling this is exactly the same as calling
	 * {@code optional(Object, true)}.
	 * 
	 * @param defaultValue
	 * 		A mandatory default value when argument is not found from the command line.
	 * 
	 * @return
	 * 		The argument object for chaining.
	 */
	public Argument<T> optional(T defaultValue)
	{
		return optional(defaultValue, true);
	}
	
	/**
	 * Sets the argument to be an optional one.
	 * 
	 * @param defaultValue
	 * 		A mandatory default value when argument is not found from the command line.
	 * 
	 * @param showDefaultValueDescription
	 * 		{@code true} if the automatic help text is to be shown for the set default value.
	 * 		{@code false} if the help text is not wanted.
	 * 
	 * @return
	 * 		The argument object for chaining.
	 */
	public Argument<T> optional(T defaultValue, boolean showDefaultValueDescription)
	{
		if(defaultValue == null)
			throw
				new ConfigurationException(
					"'defaultValue' for '" + _argument.name() + "' cannot be null."
				);
		
		_defaultForOptional = defaultValue;
		_showDefaultValueDescription = showDefaultValueDescription;
		return this;
	}
	
	/**
	 * Sets the constraint for the argument.
	 * 
	 * @param constraint
	 * 		The constraint to set.
	 * 
	 * @return
	 * 		The argument object for chaining.
	 */
	public Argument<T> constraint(Constraint<T> constraint)
	{
		String forName = _argument.name() == null ? "" : " for " + _argument.name();
		if(constraint == null)
			throw new ConfigurationException("'constraint'" + forName + " must have a value.");
		if(_hasEnumConstraint)
			throw
				new ConfigurationException(
					"Only one Enumeration constraint can be defined" + forName + "."
				);
		if(constraint instanceof Enumeration<?>)
			_hasEnumConstraint = true;
		if(constraint.description() == null || constraint.description().toParagraphs().size() == 0)
			throw new ConfigurationException("A missing constraint description" + forName + ".");
		
		_constraints.add(constraint);
		return this;
	}

	/**
	 * Sets the exact length (in number of characters) for the argument. This is a shortcut for
	 * calling {@code constraint(new Length(int))}.
	 * <p>
	 * This method is only meaningful for {@link String} arguments. 
	 * 
	 * @param length
	 * 		The length of the {@link String} argument.
	 * 
	 * @return
	 * 		The argument object for chaining.
	 */
	@SuppressWarnings("unchecked")
	public Argument<T> length(int length)
	{
		return constraint((Constraint<T>)new Length(length));
	}
	
	/**
	 * Sets the minimum length (in number of characters) for the argument. This is a shortcut for
	 * calling {@code constraint(new MinLength(int))}.
	 * <p>
	 * This method is only meaningful for {@link String} arguments. 
	 * 
	 * @param minLength
	 * 		The minimum length of the {@link String} argument.
	 * 
	 * @return
	 * 		The argument object for chaining.
	 */
	@SuppressWarnings("unchecked")
	public Argument<T> minLength(int minLength)
	{
		return constraint((Constraint<T>)new MinLength(minLength));
	}
	
	/**
	 * Sets the maximum length (in number of characters) for the argument. This is a shortcut for
	 * calling {@code constraint(new MaxLength(int))}.
	 * <p>
	 * This method is only meaningful for {@link String} arguments. 
	 * 
	 * @param maxLength
	 * 		The maximum length of the {@link String} argument.
	 * 
	 * @return
	 * 		The argument object for chaining.
	 */
	@SuppressWarnings("unchecked")
	public Argument<T> maxLength(int maxLength)
	{
		return constraint((Constraint<T>)new MaxLength(maxLength));
	}
	
	/**
	 * Sets the minimum value for the argument. This is a shortcut for calling
	 * {@code constraint(new MinValue(Object))}. The value type must be {@link Comparable}.
	 * 
	 * @param minValue
	 * 		The minimum value for the argument.
	 * 
	 * @return
	 * 		The argument object for chaining.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Argument<T> minValue(T minValue)
	{
		return constraint(new MinValue((Comparable)minValue));
	}
	
	/**
	 * Sets the maximum value for the argument. This is a shortcut for calling
	 * {@code constraint(new MaxValue(Object))}. The value type must be {@link Comparable}.
	 * 
	 * @param maxValue
	 * 		The maximum value for the argument.
	 * 
	 * @return
	 * 		The argument object for chaining.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Argument<T> maxValue(T maxValue)
	{
		return constraint(new MaxValue((Comparable)maxValue));
	}


	/**
	 * {@code Argument.Data} is a container class for holding argument values given from the command
	 * line for options and command arguments. <p>
	 * 
	 * This class is immutable <b>only if {@code T} is an immutable class</b>.
	 * 
	 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
	 *
	 * @param <T>
	 * 		Type of argument.
	 */
	public static final class Data<T>
		extends
			DataBase
	{
		private final T _value;
		private final boolean _optional;
		
		/**
		 * "Copy" constructs a data object from the internal argument object.
		 * 
		 * @param internal
		 * 		The internal argument object.
		 */
		Data(Internal<T> internal)
		{
			// Arguments do not have alternatives and thus an empty set is a proper value. 
			super(internal.name(), internal.id(), new HashSet<String>());
			_value = internal.value();
			_optional = internal.optional();
		}

		/**
		 * Returns the argument value.
		 * 
		 * @return
		 * 		The value from the command line.
		 */
		public T getValue()
		{
			return _value;
		}
		

		/**
		 * Tells if the argument is optional or not.
		 * @return
		 * 		{@code true} if optional, {@code false} otherwise.
		 */
		public boolean isOptional()
		{
			return _optional;
		}
		
		
		/**
		 * Returns a {@code String} representation of {@code Argument.Data} object. The form is:
		 * <p>
		 * <code>{NAME(ID) = VALUE (OPTIONAL)}</code>
		 * <p>
		 * where:
		 * 	<ul>
		 * 		<li>NAME is the argument name.</li>
		 * 		<li>ID is the id for the argument.</li>
		 * 		<li>VALUE is the argument value.</li>
		 * 		<li>OPTIONAL indicates if the value is optional or not.</li>
		 * 	</ul>
		 */
		@Override
		public String toString()
		{
			return
				"{" + getName() + "(" + getId() + ") = " + getValue() + " (" + isOptional() + ")}";
		}
	}
	
	
	static final class Internal<T>
		implements
			Parser,
			Cloneable
	{
		private Argument<T> _outer;
		private final Class<T> _argumentTypeClass;
		private T _value;
		
		public Internal(Argument<T> outer, Class<T> argumentTypeClass)
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
		public T value()
		{
			return _value;
		}
		public List<Constraint<T>> constraints()
		{
			return Collections.unmodifiableList(_outer._constraints);
		}
		public boolean optional()
		{
			return _outer._defaultForOptional != null;
		}
		public String defaultValueAsString()
		{
			return _outer._defaultForOptional.toString();
		}
		public String defaultValueDescription()
		{
			if(optional() && _outer._showDefaultValueDescription)
				return 
					new Description() {{
						d("Default value is ");
						if(defaultValueAsString().length() > 0)
							b(defaultValueAsString()).d(".");
						else
							d("an empty string.");
					}}.toParagraphs().get(0);
			else
				return "";
		}
		public boolean parse(List<String> arguments)
			throws
				ConstraintException,
				IllegalCommandLineArgumentException
		{
			if(arguments.size() == 0 || Util.checkOptionNaming(arguments.get(0)))
				if(optional()) {
					if(defaultValue() != null)
						((LinkedList<String>)arguments).addFirst(defaultValue().toString());
					else
						return true;
				}
				else
					return false;

			value(_argumentTypeClass.cast(valueOf(arguments.get(0), _argumentTypeClass)));
			arguments.remove(0);
			return true;
		}
		public void checkConstraints() throws ConstraintException
		{
			for(Constraint<T> constraint : constraints())
				constraint.evaluate(name(), value());
		}
		
		public Data<T> createDataObject()
		{
			return new Data<T>(this);
		}
		
		@Override
		public Internal<T> clone()
		{
			Internal<T> internal = new Internal<T>(new Argument<T>(_outer), _argumentTypeClass);
			internal._value = _value;
			return internal;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj == this)
				return true;

			if(!(obj instanceof Argument.Internal<?>))
				return false;
			Argument.Internal<?> internal = (Argument.Internal<?>)obj;
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
			return "{" + name() + "(" + id() + ") = " + value() + " (" + optional() + ")}";
		}
		
		void setDefaultValue()
		{
			value(defaultValue());
		}
		
		private void value(T value)
		{
			_value = value;
		}

		private T defaultValue()
		{
			return _outer._defaultForOptional;
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
					"'" + valueAsString + "' cannot be interpreted as "
						+ argumentTypeClass.getCanonicalName()
						+ " for '" + name() + "'.";
				throw new IllegalCommandLineArgumentException(msg, ex);
			}
		}
	}
}
