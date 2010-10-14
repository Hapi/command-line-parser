package com.hapiware.utils.cmdline;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import com.hapiware.utils.cmdline.annotation.Id;
import com.hapiware.utils.cmdline.constraint.AnnotatedFieldSetException;
import com.hapiware.utils.cmdline.constraint.ConstraintException;
import com.hapiware.utils.cmdline.constraint.IllegalCommandLineArgumentException;
import com.hapiware.utils.cmdline.element.Argument;
import com.hapiware.utils.cmdline.element.Option;


public class Util
{
	private static final String CR = "\n";
	
	
	public static boolean checkOption(
		String arg,
		List<String> cmdLineArgs,
		Map<String, Option.Inner> definedOptions,
		Map<String, String> definedOptionAlternatives,
		Set<Option.Inner> nonMultipleOptionCheckSet,
		List<Option.Inner> cmdLineOptions
	) throws ConstraintException, IllegalCommandLineArgumentException
	{
		Option.Inner option = definedOptions.get(definedOptionAlternatives.get(arg));
		if(option != null)
			option = new Option.Inner(option);
		if(option != null && !option.multiple()) {
			if(nonMultipleOptionCheckSet.contains(option)) {
				String msg = "Option '" + option.name() + "' can occur only once.";
				throw new IllegalCommandLineArgumentException(msg);
			}
			else
				nonMultipleOptionCheckSet.add(option);
		}
		if(option != null && option.parse(cmdLineArgs)) {
			// Option found.
			if(option.argument() != null)
				option.argument().checkConstraints();
			cmdLineOptions.add(option);
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean checkArguments(
		String commandName,
		List<String> cmdLineArgs,
		Map<String, Argument.Inner<?>> definedArguments,
		List<Argument.Inner<?>> outputArguments
	) throws ConstraintException, IllegalCommandLineArgumentException
	{
		int numberOfOptionalArguments = 0;
		Set<Entry<String, Argument.Inner<?>>> entrySet = definedArguments.entrySet();
		for(Iterator<?> it = entrySet.iterator(); it.hasNext();) {
			Entry<String, Argument.Inner<?>> entry = (Entry<String, Argument.Inner<?>>)it.next();
			if(entry.getValue().optional())
				numberOfOptionalArguments++;
		}

		int numberOfMandatoryArguments = entrySet.size() - numberOfOptionalArguments;
		int numberOfMaximumArguments = numberOfMandatoryArguments + numberOfOptionalArguments;
		int mandatoryOptionalDiff = numberOfMaximumArguments - numberOfMandatoryArguments;
		int numberOfCmdLineArguments = 0;
		
		// There cannot be options between command arguments. Only before or after
		// all the command arguments.
		for(String cmdLineArg : cmdLineArgs)
			if(cmdLineArg.startsWith("-"))
				break;
			else
				numberOfCmdLineArguments++;
		
		if(numberOfCmdLineArguments < numberOfMandatoryArguments) {
			String msg =
				"Too few command line arguments for command '" + commandName + "'. "
					+ "Expected min: " + numberOfMandatoryArguments
					+ " but was: " + numberOfCmdLineArguments;
			throw new IllegalCommandLineArgumentException(msg);
		}
		if(numberOfCmdLineArguments > numberOfMaximumArguments) {
			String msg =
				"Too many command line arguments for command '" + commandName + "'. "
					+ "Expected max: " + numberOfMaximumArguments
					+ " but was: " + numberOfCmdLineArguments;
			throw new IllegalCommandLineArgumentException(msg);
		}

		for(Iterator<?> it = entrySet.iterator(); it.hasNext();) {
			Entry<String, Argument.Inner<?>> entry = (Entry<String, Argument.Inner<?>>)it.next();
			Argument.Inner<?> argument = entry.getValue();
			if(argument.optional() && numberOfCmdLineArguments < entrySet.size())
				if(mandatoryOptionalDiff == 1) {
					// Adds a default value to one optional argument.
					if(it.hasNext())
						//argument = ((Entry<String, Argument.Inner<?>>)it.next()).getValue();
						((LinkedList<String>)cmdLineArgs).addFirst(argument.defaultValueAsString());
					else
						break;
				}
				else
					// Adds default values to the rest of the optional arguments
					// (which must be at end of the command definition).
					//break;
					((LinkedList<String>)cmdLineArgs).addFirst(argument.defaultValueAsString());
			
			if(argument.parse(cmdLineArgs)) {
				argument.checkConstraints();
				outputArguments.add(argument);
			}
		}
		return true;
	}
	

	public static void setAnnotatedOptions(
		Object callerObject,
		Class<?> callerClass,
		List<Option.Inner> cmdLineOptions
	)
		throws AnnotatedFieldSetException
	{
		Map<String, List<Object>> multipleOptions =
			new HashMap<String, List<Object>>();
		for(Option.Inner cmdLineOption : cmdLineOptions) {
			if(cmdLineOption.argument() != null) {
				String id = cmdLineOption.argument().id();
				if(cmdLineOption.multiple()) {
					List<Object> multipleOptionValues = multipleOptions.get(id);
					if(multipleOptionValues == null) {
						multipleOptionValues = new ArrayList<Object>();
						multipleOptions.put(id, multipleOptionValues);
					}
					multipleOptionValues.add(cmdLineOption.argument().value());
				}
				else
					Util.setAnnotatedValue(
						callerObject,
						callerClass,
						cmdLineOption.argument().value(),
						id
					);
			}
			else
				// If an option does not have any arguments defined
				// then only its existence is marked.
				Util.setAnnotatedValue(callerObject, callerClass, true, cmdLineOption.id());
		}
		for(Map.Entry<String, List<Object>> multiOption : multipleOptions.entrySet()) {
			Util.setAnnotatedValue(
				callerObject,
				callerClass,
				multiOption.getValue().toArray(new Object[0]),
				multiOption.getKey()
			);
		}
	}

	
	public static void setAnnotatedArguments(
		Object callerObject,
		Class<?> callerClass,
		List<Argument.Inner<?>> cmdLineArguments
	)
		throws
			AnnotatedFieldSetException
	{
		for(Argument.Inner<?> cmdLineArgument : cmdLineArguments)
			setAnnotatedValue(callerObject, callerClass, cmdLineArgument.value(), cmdLineArgument.id());
	}
	
	
	public static <T> void setAnnotatedValue(
		Object callerObject,
		Class<?> callerClass,
		T value,
		String valueId
	)
		throws
			AnnotatedFieldSetException
	{
		if(callerObject != null)
			callerClass = callerObject.getClass();
		if(callerClass == null)
			throw new NullPointerException("'callerClass' (or 'callerObject') must have a value.");

		setValues(callerObject, callerClass.getDeclaredFields(), value, valueId);
		setValues(callerObject, callerClass.getFields(), value, valueId);
	}
	
	private static <T> void setValues(
		Object obj,
		Field[] fields,
		T value,
		String valueId
	)
		throws
			AnnotatedFieldSetException
	{
		try {
			for(final Field f : fields) {
				Id id = f.getAnnotation(Id.class);
				if(id != null && id.value().equals(valueId)) {
					AccessController.doPrivileged(
						new PrivilegedAction<T>()
						{
							public T run()
							{
								f.setAccessible(true);
								return null;
							}
						}
					);
					if(f.getType().isArray()) {
						int length = Array.getLength(value);
						if(length > 0) {
							Object[] origArray = (Object[])value;
							Object array = Array.newInstance(f.getType().getComponentType(), length);
							for(int i = 0; i < length; i++)
								Array.set(array, i, origArray[i]);
							f.set(obj, array);
						}
					}
					else
						f.set(obj, value);
				}
			}
		}
		catch(NullPointerException e) {
			if(obj == null) {
				String msg =
					"Object reference for the field annotated '" + valueId + "' "
						+ "is missing while required. Most probably the default parse() "
						+ "method failed finding the correct object reference. Use either "
						+ "parse(Object,String[]) or parse(Class<?>,String[]).";
				throw new AnnotatedFieldSetException(msg, e);
			}
			else
				throw e;
		}
		catch(SecurityException e) {
			String msg =
				"Security is turned on and the field annotated '" + valueId + "' cannot be accessed. "
					+ "Grant access to 'java.lang.reflect.ReflectPermission \"suppressAccessChecks\"'.";
			throw new AnnotatedFieldSetException(msg, e);
		}
		catch(IllegalArgumentException e) {
			String msg =
				"[" + value + "] is an illegal argument for the field annotated '"
					+ valueId + "'. " + e.getMessage();
			throw new AnnotatedFieldSetException(msg, e);
		}
		catch(IllegalAccessException e) {
			String msg = "Should not be here but here we are...";
			throw new AnnotatedFieldSetException(msg, e);
		}
	}
	
	
	public static Object valueOf(String valueAsString, Class<?> argumentTypeClass)
	{
		if(argumentTypeClass == Integer.class)
			return Integer.valueOf(valueAsString);
		if(argumentTypeClass == Long.class)
			return Long.valueOf(valueAsString);
		if(argumentTypeClass == Byte.class)
			return Byte.valueOf(valueAsString);
		if(argumentTypeClass == Short.class)
			return Short.valueOf(valueAsString);
		if(argumentTypeClass == Double.class)
			return Double.valueOf(valueAsString);
		if(argumentTypeClass == Float.class)
			return Float.valueOf(valueAsString);
		if(argumentTypeClass == BigDecimal.class)
			return new BigDecimal(valueAsString);
		if(argumentTypeClass == BigInteger.class)
			return new BigInteger(valueAsString);
		return valueAsString;
	}

	
	public static void write(String text, int column, int width, OutputStream out)
	{
		if(column >= width)
			throw new IllegalArgumentException("'width' must be greater than 'column'.");
		
		String tab = "";
		for(int i = 0; i < column; i++)
			tab += " ";

		String toWrite = tab;
		int pos = column;
		StringTokenizer tokenizer = new StringTokenizer(text);
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if(pos + token.length() > width) {
				pos = column;
				toWrite += CR + tab;
				if(token.length() > width - column) {
					String rest = token;
					while(rest.length() > width - column) {
						toWrite += rest.substring(0, width - column) + CR + tab;
						rest = rest.substring(width - column);
					}
					token = rest;
				}
			}
			pos += token.length() + 1;
			toWrite += token + " ";
		}
		if(toWrite.trim().length() > 0)
			toWrite += CR;
		try {
			out.write(toWrite.getBytes());
		}
		catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
