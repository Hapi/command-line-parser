package com.hapiware.util.cmdlineparser;

import com.hapiware.util.cmdlineparser.constraint.Constraint;


/**
 * {@code OptionArgument} is used to define an argument for the option and command option.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @param <T>
 * 		A type parameter for the argument.
 * 
 * @see Option#set(Class, OptionArgument)
 * @see CommandLineParser#add(Option)
 * @see Command#add(Option)
 */
public class OptionArgument<T>
	extends
		Argument<T>
{
	/**
	 * Creates new {@code OptionArgument}.
	 */
	public OptionArgument()
	{
		this("value");
	}
	
	/**
	 * Creates new {@code OptionArgument}.
	 * 
	 * @param name
	 * 		A name for the argument value. This is only for the help system and is not used
	 * 		otherwise.
	 */
	public OptionArgument(String name)
	{
		super(name);
	}
	
	/**
	 * Do not call this method because the id cannot be set for {@code OptionArgument}. Calling
	 * {@link #id(String)} throws {@link ConfigurationException}.
	 */
	public OptionArgument<T> id(String id)
	{
		throw new ConfigurationException("'id' cannot be set for OptionArgument");
	}
	
	public OptionArgument<T> description(String description)
	{
		super.description(description);
		return this;
	}
	
	public OptionArgument<T> strong(String text)
	{
		super.strong(text);
		return this;
	}
	
	public OptionArgument<T> paragraph()
	{
		super.paragraph();
		return this;
	}
	
	public OptionArgument<T> d(String description)
	{
		super.d(description);
		return this;
	}
	
	public OptionArgument<T> b(String text)
	{
		super.b(text);
		return this;
	}
	
	public OptionArgument<T> p()
	{
		super.p();
		return this;
	}

	public OptionArgument<T> optional(T defaultValue)
	{
		super.optional(defaultValue);
		return this;
	}
	
	public OptionArgument<T> optional(T defaultValue, boolean showDefaultValueDescription)
	{
		super.optional(defaultValue, showDefaultValueDescription);
		return this;
	}
	
	public OptionArgument<T> constraint(Constraint<T> constraint)
	{
		super.constraint(constraint);
		return this;
	}
	
	public OptionArgument<T> length(int length)
	{
		super.length(length);
		return this;
	}
	
	public OptionArgument<T> minLength(int minLength)
	{
		super.minLength(minLength);
		return this;
	}
	
	public OptionArgument<T> maxLength(int maxLength)
	{
		super.maxLength(maxLength);
		return this;
	}
	
	public OptionArgument<T> minValue(T minValue)
	{
		super.minValue(minValue);
		return this;
	}
	
	public OptionArgument<T> maxValue(T maxValue)
	{
		super.maxValue(maxValue);
		return this;
	}
	
	/**
	 * This is used to set an id for the {@code OptionArgument}. This method is for the internal
	 * use only.
	 * 
	 * @param id
	 * 		An id for the option argument.
	 */
	void setId(String id)
	{
		super.id(id);
	}
}
