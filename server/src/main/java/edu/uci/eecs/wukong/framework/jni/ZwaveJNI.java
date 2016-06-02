package edu.uci.eecs.wukong.framework.jni;

public class ZwaveJNI {
	
	/**
	 * Sets the address to connect to
	 */
	public native void init(String address);
	
	/**
	 * Sends a list of bytes to a node
	 */
	public native void send(String address, byte[] data);
	
	/**
	 * Receive data from dongle
	 */
	public native Object[] receive(int waitmsecond);
	
	/**
	 * Goes into stop mode
	 */
	public native void stop();
	
	/**
	 * Goes into add mode
	 */
	public native void add();
	
	/**
	 * Goes into delete mode
	 */
	public native void delete();
	
	/**
	 * Polling current status
	 */
	public native String poll();
	
	/**
	 * Gets discover nodes
	 */
	public native String[] discover();
	
	/**
	 * Gets node neighbors
	 */
	public native String[] routing(String address);
	
	/**
	 * Gets device type of a node
	 */
	public native void getDeviceType();
	
	/**
	 * Get Z-Wave address (network Id 4 bytes + node ID 1 byte
	 */
	public native String getAddr();
	
	/**
	 * Hard reset
	 */
	public native void hardReset();
	
	/**
	 * check if the node fails or not
	 */
	public native boolean isNodeFail(int nodeId);
	
	/**
	 * Removes a failed node
	 */
	public native boolean removeFail(int nodeId);
	
	/**
	 * Turn debug info on or off
	 */
	public native void setVerbose(int value);
	
	/**
	 * Turn verbose on or off
	 */
	public native void setDebug(int value);
	
	/**
	 * Sets a value to a node
	 */
	public native void basicSet(int nodeId, int value);
}
