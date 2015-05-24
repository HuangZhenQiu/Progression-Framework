package edu.uci.eecs.wukong.framework.load;

import java.util.HashMap;
import java.lang.Class;

public class PluginClassLoader extends FrameworkClassLoader{
	private HashMap<String, Class> plugins;

	public synchronized Class loadClass(String className) {
		
	}
}
