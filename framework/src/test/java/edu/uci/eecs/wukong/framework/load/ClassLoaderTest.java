package edu.uci.eecs.wukong.framework.load;

import java.lang.reflect.Constructor;

import edu.uci.eecs.wukong.framework.prclass.PrClass;

public class ClassLoaderTest {
	
	public static Class<?> loadClass(String name) throws ClassNotFoundException {
		ClassLoader loader = ClassLoaderTest.class.getClassLoader();
		return loader.loadClass(name);
	}

	public static void main(String[] args) {
		ClassLoaderTest test = new ClassLoaderTest();
		try {
			Class c = test.loadClass("edu.uci.eecs.wukong.plugin.demo.DemoPlugin");
			Constructor ctor = c.getConstructor();
			PrClass object  = (PrClass)ctor.newInstance();
			System.out.println(object.registerContext());
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
