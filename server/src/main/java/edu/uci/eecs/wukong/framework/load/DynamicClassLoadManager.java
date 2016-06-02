package edu.uci.eecs.wukong.framework.load;

public class DynamicClassLoadManager {
	private FrameworkClassLoader frameworkClassLoader;
	private PluginClassLoader prClassLoader;
	
	public DynamicClassLoadManager() {
		frameworkClassLoader = new FrameworkClassLoader();
		prClassLoader = new PluginClassLoader(frameworkClassLoader);
	}
	
	
}
