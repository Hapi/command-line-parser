package com.hapiware.utils.cmdline.constraint;

public interface Constraint
{
	public void evaluate(String argumentName, Object value) throws ConstraintException;
}
