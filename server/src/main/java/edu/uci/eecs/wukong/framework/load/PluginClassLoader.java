package edu.uci.eecs.wukong.framework.load;

import java.util.HashMap;
import java.lang.Class;

public class PluginClassLoader extends ClassLoader{
	
	public PluginClassLoader(FrameworkClassLoader parent) {
		super(parent);
	}
	private HashMap<String, Class<?>> plugins;

	public synchronized Class<?> loadClass(String className, boolean resolve) {
		Class<?> c = plugins.get(className);
		if (c == null) {
			try {
				if (this.getParent() != null) {
					c = this.getParent().loadClass(className);
				}
			} catch (ClassNotFoundException e) {
				c = findClass(className);
			}
		}
		if (resolve) {
			resolveClass(c);
		}
		
		return c;
	}
	
	@Override
	protected Class<?> findClass(String name) {
        return Class.class;
    }
}
