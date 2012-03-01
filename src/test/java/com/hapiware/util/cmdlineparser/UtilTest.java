package com.hapiware.util.cmdlineparser;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import org.testng.annotations.Test;

public class UtilTest
{
	@Test
	public void checkNameTrue()
	{
		assertTrue(Util.checkName("a"));
		assertTrue(Util.checkName("ab"));
		assertTrue(Util.checkName("abc"));
		assertTrue(Util.checkName("temp-5-6-check"));
		assertTrue(Util.checkName("a_five-8"));
		assertTrue(Util.checkName("miu_mau_mou"));
	}

	@Test
	public void checkNameFalse()
	{
		assertFalse(Util.checkName("a bc"));
		assertFalse(Util.checkName("5"));
		assertFalse(Util.checkName("5check"));
		assertFalse(Util.checkName("5-check"));
		assertFalse(Util.checkName("5_check"));
		assertFalse(Util.checkName("_miu"));
		assertFalse(Util.checkName("-miu"));
	}
	
	@Test
	public void checkOptionNameTrue()
	{
		assertTrue(Util.checkOptionNaming("-a"));
		assertTrue(Util.checkOptionNaming("-b"));
		assertTrue(Util.checkOptionNaming("--verbose"));
		assertTrue(Util.checkOptionNaming("--checked-argument"));
		assertTrue(Util.checkOptionNaming("--checked_argument"));
		assertTrue(Util.checkOptionNaming("--a5"));
		assertTrue(Util.checkOptionNaming("--a5-6"));
	}

	@Test
	public void checkOptionNameFalse()
	{
		assertFalse(Util.checkOptionNaming("a"));
		assertFalse(Util.checkOptionNaming("-2"));
		assertFalse(Util.checkOptionNaming("-verbose"));
		assertFalse(Util.checkOptionNaming("-- checked-argument"));
		assertFalse(Util.checkOptionNaming("--5point"));
		assertFalse(Util.checkOptionNaming("--a"));
	}
}
