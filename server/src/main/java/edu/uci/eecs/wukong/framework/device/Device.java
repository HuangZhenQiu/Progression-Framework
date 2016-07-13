package edu.uci.eecs.wukong.framework.device;

public class Device {
	private long networkId;
	
	public Device(long networkId) {
		this.networkId = networkId;
	}
	
	public long getNetworkId() {
		return networkId;
	}
}
