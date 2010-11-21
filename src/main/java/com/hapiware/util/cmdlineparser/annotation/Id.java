package com.hapiware.util.cmdlineparser.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * {@code @Id} is used to mark member fields to get the values from the command line parser
 * automatically. For more information see
 * <a href="../CommandLineParser.html#cmdlineparser-annotations">CommandLineParser, chapter Annotations</a> 
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id
{
	String value();
}
