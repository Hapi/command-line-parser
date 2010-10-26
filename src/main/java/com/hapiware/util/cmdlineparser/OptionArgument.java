package com.hapiware.util.cmdlineparser;

import com.hapiware.util.cmdlineparser.constraint.Constraint;


public class OptionArgument<T>
	extends
		Argument<T>
{
	public OptionArgument()
	{
		// Does nothing.
	}
	
	public OptionArgument<T> id(String id)
	{
		super.id(id);
		return this;
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
}
