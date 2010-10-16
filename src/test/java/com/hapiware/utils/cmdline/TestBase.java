package com.hapiware.utils.cmdline;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;

abstract public class TestBase
{
	private static Map<?, ?> getPackages(ClassLoader classLoader)
	{
		try {
			Class<?> javaLangClassLoader =
				classLoader.getClass().getSuperclass().getSuperclass().getSuperclass();
			Field packages = javaLangClassLoader.getDeclaredField("packages");
			packages.setAccessible(true);
			return (Map<?, ?>)packages.get(classLoader);
		}
		catch(IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static void replacePackage(Class<?> baseClass)
	{
		String key = baseClass.getPackage().getName(); 
		try {
			Map<String, Package> map = (Map<String, Package>)getPackages(baseClass.getClassLoader());
			map.remove(key);
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
					"0.0.0",
					"hapi",
					"command-line-parser",
					"1.0.0",
					"http://www.hapiware.com",
					null,
					baseClass.getClassLoader()
			);
			map.put(key, pkg);
			
			/*Field parent = javaLangClassLoader.getDeclaredField("parent");
			parent.setAccessible(true);
			ClassLoader parentClassLoader = (ClassLoader)parent.get(cl);
			Map<?, ?> parentMap = getPackages(parentClassLoader);*/
		}
		catch(SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static {
		replacePackage(TestBase.class);
	}
}
