package edu.uci.eecs.wukong.framework.load;

import java.lang.reflect.Constructor;

import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;

public class ClassLoaderTest {
	
	public static Class<?> loadClass(String name) throws ClassNotFoundException {
		ClassLoader loader = ClassLoaderTest.class.getClassLoader();
		return loader.loadClass(name);
	}

	public static void main(String[] args) {
		ClassLoaderTest test = new ClassLoaderTest();
		try {
			Class c = test.loadClass("edu.uci.eecs.wukong.prclass.demo.DemoPrClass");
			Constructor ctor = c.getConstructor(PrClassMetrics.class);
			PipelinePrClass object  = (PipelinePrClass)ctor.newInstance(new PrClassMetrics(null));
			System.out.println(object.registerContext());
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
