package edu.uci.eecs.wukong.framework.plugin;

import java.util.List;

/**
 * The plugin initialization is splited into two stages:
 * 1) Create instances at beginning
 * 2) Bind to pipeline 
 * 
 * This interface is used for wkpf to notify plugin manager to bind
 * local plugins in component map to pipeline
 * 
 * @author Peter
 *
 */
public interface PluginInitListener {
	
	public void bindPlugins(List<Plugin> plugins);
}
