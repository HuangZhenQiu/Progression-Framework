package edu.uci.eecs.wukong.framework.load;

import java.lang.ClassLoader;
import java.util.HashMap;
import java.lang.Class;

public class FrameworkClassLoader extends ClassLoader {
	private HashMap<String, Class<?>> classes;
	
	public FrameworkClassLoader() {
		super(FrameworkClassLoader.class.getClassLoader());
		this.classes = new HashMap<String, Class<?>>();
	}
	
	public synchronized Class<?> loadClass(String className) {
		Class<?> result = (Class<?>)classes.get(className); 
        if (result == null){
        	try {
        	    result = super.getClass().getClassLoader().loadClass(className);
        	    classes.put(className, result);
        	    return result; 
    	    } catch (ClassNotFoundException e) { 
    	         System.out.println(e.toString());
    	    } 
        }
        
        return result;
	}
}
