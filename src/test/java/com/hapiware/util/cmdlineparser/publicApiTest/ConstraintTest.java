package com.hapiware.util.cmdlineparser.publicApiTest;

import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hapiware.util.cmdlineparser.AnnotatedFieldSetException;
import com.hapiware.util.cmdlineparser.Argument;
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.CommandNotFoundException;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.IllegalCommandLineArgumentException;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;
import com.hapiware.util.cmdlineparser.constraint.Enumeration;


public class ConstraintTest
	extends
		TestBase
{
	private CommandLineParser _parser;
	
	@BeforeMethod
	public void init()
	{
		_parser =
			new CommandLineParser(
				ConstraintTest.class,
				new Description().description("Main description.")
			);
		_parser.add(Integer.class, new Argument<Integer>("MINMAX") {{
			description("Description for MINMAX.");
			minValue(-5);
			maxValue(10);
		}});
		_parser.add(String.class, new Argument<String>("LENGTH") {{
			description("Description for LENGTH.");
			minLength(4);
			maxLength(7);
		}});
		_parser.add(String.class, new Argument<String>("ENUMVAL") {{
			description("Description for ENUMVAL.");
			constraint(new Enumeration<String>() {{
				value("4", "Four");
				valueIgnoreCase("a", "Ignore case a.");
				value("X", "X");
			}});
		}});
		_parser.add(Integer.class, new Argument<Integer>("ENUMRANGE") {{
			description("Description for ENUMRANGE.");
			constraint(new Enumeration<Integer>() {{
				includeRange(-15, 15, "Include range.");
				excludeRange(-3, 3, "Exclude range.");
			}});
		}});
		_parser.add(Integer.class, new Argument<Integer>("ENUMMIX") {{
			description("Description for ENUMMIX.");
			constraint(new Enumeration<Integer>() {{
				includeRange(-15, 15, "Include range.");
				excludeRange(-3, 3, "Exclude range.");
				value(0, "Zero accepted.");
				value(2, "Two accepted.");
			}});
		}});
	}

	
	
	/************************************
	 * 
	 * MINMAX Begin
	 * 
	 ************************************/
	@Test
	public void testMinMaxSucces()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		for(int i = -5; i <= 10; i++)
			_parser.parse(
				new String[] { Integer.toString(i) , "12345", "4", "-15", "-15" }
			);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"'-6' is smaller than the minimum value -5 allowed for 'MINMAX'\\."
	)
	public void testMinMaxTooSmall()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-6" , "12345", "4", "-15", "-15" }
		);
	}

	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"'-616' is smaller than the minimum value -5 allowed for 'MINMAX'\\."
	)
	public void testMinMaxTooSmall2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "-616" , "12345", "4", "-15", "-15" }
		);
	}

	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"'11' is greater than the maximum value 10 allowed for 'MINMAX'\\."
	)
	public void testMinMaxTooBig()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "11" , "12345", "4", "-15", "-15" }
		);
	}

	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"'616' is greater than the maximum value 10 allowed for 'MINMAX'\\."
	)
	public void testMinMaxTooBig2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "616" , "12345", "4", "-15", "-15" }
		);
	}
	// MINMAX End

	
	
	
	/************************************
	 * 
	 * LENGTH Begin
	 * 
	 ************************************/
	@Test
	public void testLengthSucces()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		String str = "123";
		for(int i = 4; i <= 7; i++) {
			str += i;
			_parser.parse(
				new String[] { "1" , str, "4", "-15", "-15" }
			);
		}
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Length of '123' is shorter than the minimum length 4 allowed for 'LENGTH'\\."
	)
	public void testLengthTooShort()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "123", "4", "-15", "-15" }
		);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Length of '1' is shorter than the minimum length 4 allowed for 'LENGTH'\\."
	)
	public void testLengthTooShort2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "1", "4", "-15", "-15" }
		);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Length of '12345678' is longer than the maximum length 7 allowed for 'LENGTH'\\."
	)
	public void testLengthTooLong()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345678", "4", "-15", "-15" }
		);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Length of '1234567890' is longer than the maximum length 7 allowed for 'LENGTH'\\."
	)
	public void testLengthTooLong2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "1234567890", "4", "-15", "-15" }
		);
	}
	
	@Test
	public void testLengthExactSuccess()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		CommandLineParser p =
			new CommandLineParser(
				ConstraintTest.class,
				new Description().description("Main description.")
			);
		p.add(String.class, new Argument<String>("LENGTH") {{
			description("Description for LENGTH.");
			length(5);
		}});
		p.parse(
			new String[] { "12345" }
		);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Length of '1234' differs from the length 5 allowed for 'LENGTH'\\."
	)
	public void testLengthExactTooShort()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		CommandLineParser p =
			new CommandLineParser(
				ConstraintTest.class,
				new Description().description("Main description.")
			);
		p.add(String.class, new Argument<String>("LENGTH") {{
			description("Description for LENGTH.");
			length(5);
		}});
		p.parse(
			new String[] { "1234" }
		);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Length of '123456' differs from the length 5 allowed for 'LENGTH'\\."
	)
	public void testLengthExactTooLong()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		CommandLineParser p =
			new CommandLineParser(
				ConstraintTest.class,
				new Description().description("Main description.")
			);
		p.add(String.class, new Argument<String>("LENGTH") {{
			description("Description for LENGTH.");
			length(5);
		}});
		p.parse(
			new String[] { "123456" }
		);
	}
	// LENGTH End

	
	
	/************************************
	 * 
	 * ENUMVAL Begin
	 * 
	 ************************************/
	@Test
	public void testEnumValue_4_Succes()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "4", "-15", "-15" }
		);
	}

	@Test
	public void testEnumValue_a_Succes()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "a", "-15", "-15" }
		);
	}

	@Test
	public void testEnumValue_A_Succes()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "A", "-15", "-15" }
		);
	}

	@Test
	public void testEnumValue_X_Succes()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "X", "-15", "-15" }
		);
	}

	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Value for 'ENUMVAL' was '3' but it must be one of these: \\[4, a, X\\]\\."
	)
	public void testEnumValue_3_Failure()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "3", "-15", "-15" }
		);
	}

	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Value for 'ENUMVAL' was 'w' but it must be one of these: \\[4, a, X\\]\\."
	)
	public void testEnumValue_w_Failure()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "w", "-15", "-15" }
		);
	}

	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Value for 'ENUMVAL' was 'x' but it must be one of these: \\[4, a, X\\]\\."
	)
	public void testEnumValue_x_Failure()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "x", "-15", "-15" }
		);
	}

	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Value for 'ENUMVAL' was 'WORD' but it must be one of these: \\[4, a, X\\]\\."
	)
	public void testEnumValue_WORD_Failure()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "WORD", "-15", "-15" }
		);
	}
	// ENUMVAL End
	
	
	/************************************
	 * 
	 * ENUMRANGE Begin
	 * 
	 ************************************/
	@Test
	public void testEnumRangeSucces()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		for(int i = -15; i <= 15; i++) {
			if(i >= -3 && i <=3)
				continue;
			_parser.parse(
				new String[] { "1" , "12345", "4", Integer.toString(i), "-15" }
			);
		}
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Value for 'ENUMRANGE' was '-16' but it must be one of these: "
				+ "\\[\\(-15 \\.\\.\\. 15\\)\\] \\!\\[\\(-3 \\.\\.\\. 3\\)\\]\\."
	)
	public void testEnumRangeTooSmall()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "4", "-16", "-15" }
		);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Value for 'ENUMRANGE' was '-616' but it must be one of these: "
				+ "\\[\\(-15 \\.\\.\\. 15\\)\\] \\!\\[\\(-3 \\.\\.\\. 3\\)\\]\\."
	)
	public void testEnumRangeTooSmall2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "4", "-616", "-15" }
		);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Value for 'ENUMRANGE' was '16' but it must be one of these: "
				+ "\\[\\(-15 \\.\\.\\. 15\\)\\] \\!\\[\\(-3 \\.\\.\\. 3\\)\\]\\."
	)
	public void testEnumRangeTooBig()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "4", "16", "-15" }
		);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Value for 'ENUMRANGE' was '616' but it must be one of these: "
				+ "\\[\\(-15 \\.\\.\\. 15\\)\\] \\!\\[\\(-3 \\.\\.\\. 3\\)\\]\\."
	)
	public void testEnumRangeTooBig2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "4", "616", "-15" }
		);
	}
	
	@Test
	public void testEnumRangeExcludedValues()
		throws
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		List<String> values = new ArrayList<String>();
		for(int i = -3; i <= 3; i++)
			values.add(Integer.toString(i));
		
		int numOfErrors = 0;
		for(String value : values) {
			try {
				_parser.parse(
					new String[] { "1" , "12345", "4", value, "-15" }
				);
			}
			catch(ConstraintException e) {
				numOfErrors++;
				String msg =
					"Value for 'ENUMRANGE' was '" + value + "' but it must be one of these: "
						+ "[(-15 ... 15)] ![(-3 ... 3)].";
				if(!e.getMessage().equals(msg)) {
					String failMessage =
						"'" + value + "' didn't throw an expection with an expected message.";
					fail(failMessage);
				}
			}
		}
		if(numOfErrors != values.size()) {
			String msg =
				"Some of the excluded values didn't throw an exception. "
					+ "Expected number of exceptions was: " + values.size()
					+ " but was: " + numOfErrors;
			fail(msg);
		}
	}
	// ENUMRANGE End

	
	
	
	/************************************
	 * 
	 * ENUMMIX Begin
	 * 
	 ************************************/
	@Test
	public void testEnumMixSucces()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		for(int i = -15; i <= 15; i++) {
			if(i >= -3 && i <=3)
				continue;
			_parser.parse(
				new String[] { "1" , "12345", "4", "-15", Integer.toString(i) }
			);
		}
		_parser.parse(
			new String[] { "1" , "12345", "4", "-15", "0" }
		);
		_parser.parse(
			new String[] { "1" , "12345", "4", "-15", "2" }
		);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Value for 'ENUMMIX' was '-16' but it must be one of these: "
				+ "\\[\\(-15 \\.\\.\\. 15\\)\\] \\!\\[\\(-3 \\.\\.\\. 3\\)\\] \\[0, 2\\]\\."
	)
	public void testEnumMixTooSmall()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "4", "-15", "-16" }
		);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Value for 'ENUMMIX' was '-616' but it must be one of these: "
				+ "\\[\\(-15 \\.\\.\\. 15\\)\\] \\!\\[\\(-3 \\.\\.\\. 3\\)\\] \\[0, 2\\]\\."
	)
	public void testEnumMixTooSmall2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "4", "-15", "-616" }
		);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Value for 'ENUMMIX' was '16' but it must be one of these: "
				+ "\\[\\(-15 \\.\\.\\. 15\\)\\] \\!\\[\\(-3 \\.\\.\\. 3\\)\\] \\[0, 2\\]\\."
	)
	public void testEnumMixTooBig()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "4", "-15", "16" }
		);
	}
	
	@Test(
		expectedExceptions = { ConstraintException.class },
		expectedExceptionsMessageRegExp =
			"Value for 'ENUMMIX' was '616' but it must be one of these: "
				+ "\\[\\(-15 \\.\\.\\. 15\\)\\] \\!\\[\\(-3 \\.\\.\\. 3\\)\\] \\[0, 2\\]\\."
	)
	public void testEnumMixTooBig2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		_parser.parse(
			new String[] { "1" , "12345", "4", "-15", "616" }
		);
	}
	
	@Test
	public void testEnumMixExcludedValues()
		throws
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		List<String> values = new ArrayList<String>();
		for(int i = -3; i <= 3; i++) {
			if(i == 0 || i == 2)
				continue;
			values.add(Integer.toString(i));
		}
		
		int numOfErrors = 0;
		for(String value : values) {
			try {
				_parser.parse(
					new String[] { "1" , "12345", "4", "-15", value }
				);
			}
			catch(ConstraintException e) {
				numOfErrors++;
				String msg =
					"Value for 'ENUMMIX' was '" + value + "' but it must be one of these: "
						+ "[(-15 ... 15)] ![(-3 ... 3)] [0, 2].";
				if(!e.getMessage().equals(msg)) {
					String failMessage =
						"'" + value + "' didn't throw an expection with an expected message. "
							+ "Message was: " + e.getMessage();
					fail(failMessage);
				}
			}
		}
		if(numOfErrors != values.size()) {
			String msg =
				"Some of the excluded values didn't throw an exception. "
					+ "Expected number of exceptions was: " + values.size()
					+ " but was: " + numOfErrors;
			fail(msg);
		}
	}
	// ENUMMIX End
}
