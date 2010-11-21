package com.hapiware.util.cmdlineparser.constraint;

import com.hapiware.util.cmdlineparser.Argument;
import com.hapiware.util.cmdlineparser.Command;
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.OptionArgument;


/**
 * {@code Constraint} interface is used to implement different constraints for {@link Argument}
 * and {@link OptionArgument} objects.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @param <T>
 * 		A type of the constraint.
 */
public interface Constraint<T>
{
	/**
	 * This method is called by the command line parser framework when new arguments are configured
	 * (added) for the command line parser. Namely the used methods for adding arguments are
	 * {@link CommandLineParser#add(Class, com.hapiware.util.cmdlineparser.Argument)} and
	 * {@link Command#add(Class, com.hapiware.util.cmdlineparser.Argument)}. {@code true} must
	 * be returned if {@code typeClass} (i.e. the type the added {@link Argument} or
	 * {@link OptionArgument}) is meaningful to check with the implemented constraint. Otherwise
	 * {@code false} must be returned.
	 * <p>
	 * For example if an implementation tests a minimum value {@link #typeCheck(Class)} then
	 * {@code true} is returned if {@code typeClass} implements {@link Comparable} interface.
	 * 
	 * @param typeClass
	 * 		A type of the argument which is set by above mentioned {@code add()} methods.
	 * 
	 * @return
	 * 		{@code true} if the argument type is proper for the implemented constraint.
	 */
	public boolean typeCheck(Class<?> typeClass);
	
	/**
	 * <b>Must throw</b> {@link ConstraintException} if the given {@code value} violates
	 * the constraint. Otherwise the {@code evaluate()} method does nothing.
	 * 
	 * @param argumentName
	 * 		The name of the argument which is used for {@link ConstraintException} message to
	 * 		identify the violating argument.
	 * 
	 * @param value
	 * 		The value to be tested against possible constraint violation.
	 * 
	 * @throws ConstraintException
	 * 		If the constraint is violated.
	 */
	public void evaluate(String argumentName, T value) throws ConstraintException;
	
	/**
	 * Creates a description of the constraint for the help system.
	 * 
	 * <h3>NOTICE!</h3>
	 * Paragraphs (calls to {@link Description#paragraph()} or {@link Description#p()}) has
	 * a special meaning for {@code Constraint}s. Every call to {@link Description#paragraph()}
	 * (or {@link Description#p()} is internally translated to a list element. Normally 
	 * a description for constraint is only a single paragraph (i.e. a chain of
	 * {@link Description#d(String)} and {@link Description#d(String)} calls) but some constraints,
	 * like enumeration for example, naturally needs a way to have a separate description for each
	 * of it's constraint. This can be handled by creating a paragraph for each of the enumerated
	 * constraint.  
	 * 
	 * @return
	 * 		A constructed description object.
	 */
	public Description description();
}
