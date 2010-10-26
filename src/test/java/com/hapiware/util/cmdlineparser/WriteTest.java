package com.hapiware.util.cmdlineparser;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.testng.annotations.Test;

public class WriteTest
{
	private final String _testString =
		"This option can occur several times. Argument is optional. Default value for "
		+ "the optional argument is '5'. Description for 'number' option.";
	
	@Test
	public void write80_4()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString, 4, 80, outStream);
		String hereDoc =
			"    This option can occur several times. Argument is optional. Default value for\n"
			+ "    the optional argument is '5'. Description for 'number' option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write80_10()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString, 10, 80, outStream);
		String hereDoc =
			"          This option can occur several times. Argument is optional. Default\n"
			+ "          value for the optional argument is '5'. Description for 'number'\n"
			+ "          option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write20_0()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString, 0, 20, outStream);
		String hereDoc =
			"This option can\n"
			+ "occur several times.\n"
			+ "Argument is\n"
			+ "optional. Default\n"
			+ "value for the\n"
			+ "optional argument is\n"
			+ "'5'. Description for\n"
			+ "'number' option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write20_4()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString, 4, 20, outStream);
		String hereDoc =
			"    This option can\n"
			+ "    occur several\n"
			+ "    times. Argument\n"
			+ "    is optional.\n"
			+ "    Default value\n"
			+ "    for the optional\n"
			+ "    argument is '5'.\n"
			+ "    Description for\n"
			+ "    'number' option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write40_5_NoSpaces()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString.replaceAll(" ", ""), 5, 40, outStream);
		String hereDoc =
			"     Thisoptioncanoccurseveraltimes.Argu\n"
			+ "     mentisoptional.Defaultvaluefortheop\n"
			+ "     tionalargumentis'5'.Descriptionfor'\n"
			+ "     number'option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write80_4_NoSpaces()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString.replaceAll(" ", ""), 4, 80, outStream);
		String hereDoc =
			"    Thisoptioncanoccurseveraltimes.Argumentisoptional.Defaultvaluefortheoptional\n"
			+ "    argumentis'5'.Descriptionfor'number'option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	
	@Test
	public void write20_4_NoSpaces()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString.replaceAll(" ", ""), 4, 20, outStream);
		String hereDoc =
			"    Thisoptioncanocc\n"
			+ "    urseveraltimes.A\n"
			+ "    rgumentisoptiona\n"
			+ "    l.Defaultvaluefo\n"
			+ "    rtheoptionalargu\n"
			+ "    mentis'5'.Descri\n"
			+ "    ptionfor'number'\n"
			+ "    option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write40_5_LongStringInBeginning()
	{
		final String localTestString =
			"Thisoptioncanoccurseveraltimes.Argumentisoptional. Default value for "
			+ "the optional argument is '5'. Description for 'number' option.";
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(localTestString, 5, 40, outStream);
		String hereDoc =
			"     Thisoptioncanoccurseveraltimes.Argu\n"
			+ "     mentisoptional. Default value for\n"
			+ "     the optional argument is '5'.\n"
			+ "     Description for 'number' option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write40_5_LongStringInMiddle()
	{
		final String localTestString =
			"This option can occur several times. Argumentisoptional.Defaultvaluefor"
			+ "theoptional argument is '5'. Description for 'number' option.";
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(localTestString, 5, 40, outStream);
		String hereDoc =
			"     This option can occur several\n"
			+ "     times.\n"
			+ "     Argumentisoptional.Defaultvaluefort\n"
			+ "     heoptional argument is '5'.\n"
			+ "     Description for 'number' option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write40_5_LongStringAtEnd()
	{
		final String localTestString =
			"This option can occur several times. Argument is optional. Default value for "
			+ "the optional argumentis'5'.Descriptionfor'number'option.";
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(localTestString, 5, 40, outStream);
		String hereDoc =
			"     This option can occur several\n"
			+ "     times. Argument is optional.\n"
			+ "     Default value for the optional\n"
			+ "     argumentis'5'.Descriptionfor'number\n"
			+ "     'option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	

	@Test
	public void write80_4_8()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString, 4, 8, 80, outStream);
		String hereDoc =
			"    This option can occur several times. Argument is optional. Default value for\n"
			+ "        the optional argument is '5'. Description for 'number' option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write80_10_5()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString, 10, 5, 80, outStream);
		String hereDoc =
			"          This option can occur several times. Argument is optional. Default\n"
			+ "     value for the optional argument is '5'. Description for 'number' option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write20_0_4()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString, 0, 4, 20, outStream);
		String hereDoc =
			"This option can\n"
			+ "    occur several\n"
			+ "    times. Argument\n"
			+ "    is optional.\n"
			+ "    Default value\n"
			+ "    for the optional\n"
			+ "    argument is '5'.\n"
			+ "    Description for\n"
			+ "    'number' option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write20_4_8()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString, 4, 8, 20, outStream);
		String hereDoc =
			"    This option can\n"
			+ "        occur\n"
			+ "        several\n"
			+ "        times.\n"
			+ "        Argument is\n"
			+ "        optional.\n"
			+ "        Default\n"
			+ "        value for\n"
			+ "        the optional\n"
			+ "        argument is\n"
			+ "        '5'.\n"
			+ "        Description\n"
			+ "        for 'number'\n"
			+ "        option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write40_5_10_NoSpaces()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString.replaceAll(" ", ""), 5, 10, 40, outStream);
		String hereDoc =
			"     Thisoptioncanoccurseveraltimes.Argu\n"
			+ "          mentisoptional.Defaultvaluefor\n"
			+ "          theoptionalargumentis'5'.Descr\n"
			+ "          iptionfor'number'option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write80_4_8_NoSpaces()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString.replaceAll(" ", ""), 4, 8, 80, outStream);
		String hereDoc =
			"    Thisoptioncanoccurseveraltimes.Argumentisoptional.Defaultvaluefortheoptional\n"
			+ "        argumentis'5'.Descriptionfor'number'option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write80_4_0_NoSpaces()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString.replaceAll(" ", ""), 4, 0, 80, outStream);
		String hereDoc =
			"    Thisoptioncanoccurseveraltimes.Argumentisoptional.Defaultvaluefortheoptional\n"
			+ "argumentis'5'.Descriptionfor'number'option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write20_4_8_NoSpaces()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(_testString.replaceAll(" ", ""), 4, 8, 20, outStream);
		String hereDoc =
			"    Thisoptioncanocc\n"
			+ "        urseveraltim\n"
			+ "        es.Argumenti\n"
			+ "        soptional.De\n"
			+ "        faultvaluefo\n"
			+ "        rtheoptional\n"
			+ "        argumentis'5\n"
			+ "        '.Descriptio\n"
			+ "        nfor'number'\n"
			+ "        option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write40_5_10_LongStringInBeginning()
	{
		final String localTestString =
			"Thisoptioncanoccurseveraltimes.Argumentisoptional. Default value for "
			+ "the optional argument is '5'. Description for 'number' option.";
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(localTestString, 5, 10, 40, outStream);
		String hereDoc =
			  "     Thisoptioncanoccurseveraltimes.Argu\n"
			+ "          mentisoptional. Default value\n"
			+ "          for the optional argument is\n"
			+ "          '5'. Description for 'number'\n"
			+ "          option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write40_5_10_LongStringInMiddle()
	{
		final String localTestString =
			"This option can occur several times. Argumentisoptional.Defaultvaluefor"
			+ "theoptional argument is '5'. Description for 'number' option.";
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(localTestString, 5, 10, 40, outStream);
		String hereDoc =
			  "     This option can occur several\n"
			+ "          times.\n"
			+ "          Argumentisoptional.Defaultvalu\n"
			+ "          efortheoptional argument is\n"
			+ "          '5'. Description for 'number'\n"
			+ "          option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void write40_5_10_LongStringAtEnd()
	{
		final String localTestString =
			"This option can occur several times. Argument is optional. Default value for "
			+ "the optional argumentis'5'.Descriptionfor'number'option.";
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(localTestString, 5, 10, 40, outStream);
		String hereDoc =
			"     This option can occur several\n"
			+ "          times. Argument is optional.\n"
			+ "          Default value for the optional\n"
			+ "          argumentis'5'.Descriptionfor'n\n"
			+ "          umber'option.";
		assertEquals(outStream.toString(), hereDoc);
	}
	
	@Test
	public void writeJustSpaces()
	{
		final String localTestString = "    ";
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(localTestString, 0, 80, outStream);
		assertEquals(outStream.toString(), "");
	}
	
	@Test
	public void writeJustSpacesWithOneNewLine()
	{
		final String localTestString = "    \n";
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(localTestString, 0, 80, outStream);
		assertEquals(outStream.toString(), "");
	}
	
	@Test
	public void writeJustSpacesWithTwoNewLines()
	{
		final String localTestString = "    \n\n";
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(localTestString, 0, 80, outStream);
		assertEquals(outStream.toString(), "");
	}
	
	@Test
	public void writeJustSpacesWithThreeNewLines()
	{
		final String localTestString = "    \n\n\n";
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Util.write(localTestString, 0, 80, outStream);
		assertEquals(outStream.toString(), "");
	}
}
