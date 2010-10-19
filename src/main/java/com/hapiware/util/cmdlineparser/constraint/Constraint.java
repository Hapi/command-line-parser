package com.hapiware.util.cmdlineparser.constraint;

import com.hapiware.util.cmdlineparser.Description;

public interface Constraint<T>
{
	public boolean typeCheck(Class<?> typeClass);
	public void evaluate(String argumentName, T value) throws ConstraintException;
	
	/**
	 * Paragraphs (calls to {@link Description#paragraph()} or {@link Description#p()}) has
	 * a special meaning for {@code Constraint}s. Every call to {@link Description#paragraph()}
	 * (or {@link Description#p()} is internally translated as to a list element.
	 * 
	 * @return
	 */
	public Description description();
}
