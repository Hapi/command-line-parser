package com.hapiware.utils.cmdline;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;

/**
 * A base class for tests. The main task of this test is to add {@code Implementation-Title} and
 * {@code Implementation-Version} information to {@link java.lang.ClassLoader} so that tests can
 * pass. The reason is {@link CommandLineParser)} constructors that check the existence of
 * the manifest properties.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
abstract public class TestBase
{
	static {
		try {
			replacePackage(TestBase.class);
		}
		catch(Throwable e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private static void replacePackage(Class<?> baseClass) throws Throwable
	{
		String key = baseClass.getPackage().getName(); 
			Class<?> pkgClass = Class.forName("java.lang.Package");
			Constructor<?> constructor = pkgClass.getDeclaredConstructor(
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				URL.class,
				ClassLoader.class
			);
			constructor.setAccessible(true);
			Package pkg = 
				(Package)constructor.newInstance(
					key,
					"TestBase",
					"1.0.0",
					"hapi",
					"cmd-parser",
					"1.0.0",
					"http://www.hapiware.com",
					null,
					baseClass.getClassLoader()
			);
			Map<String, Package> map = (Map<String, Package>)getPackages(baseClass.getClassLoader());
			//map.remove(key);
			map.put(key, pkg);
	}
	
	private static Map<?, ?> getPackages(ClassLoader classLoader) throws Throwable
	{
		Class<?> javaLangClassLoader =
			classLoader.getClass().getSuperclass().getSuperclass().getSuperclass();
		Field packages = javaLangClassLoader.getDeclaredField("packages");
		packages.setAccessible(true);
		return (Map<?, ?>)packages.get(classLoader);
	}
}
