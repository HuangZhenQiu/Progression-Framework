package edu.uci.eecs.wukong.framework.state;


/**
 * A Progression Sever's states are included in the StateModel. There are several components will change the states,
 * one is the wkpf for get the network information, the other is the plugin manager that bind wuobjects to pipeline. 
 * They will use the interface to trigger the StateManager to persist latest state information into file.
 */
public interface StateUpdatelistener {
	
	public void update();
}
