package edu.uci.eecs.wukong.framework.prclass;

import edu.uci.eecs.wukong.framework.wkpf.WKPF;

public class Poller {
	private WKPF wkpf;

	public Poller(WKPF wkpf) {
		this.wkpf = wkpf;
	}
	
	
	public void sendGetLinkCounter(Long address, short linkId) {
		this.wkpf.sendGetLinkCounter(address, linkId);
	}
	
	public void sendHeartBeatRequest(Long address) {
		this.wkpf.sendGetDeviceStatus(address);
	}
	
	public void sendSetLock(Long address, short linkId) {
		this.wkpf.sendSetLock(address, linkId);
	}
	
	public void changeLink(Long address, short linkId, short componentId, short endpointId) {
		this.wkpf.sendChangeLink(address, linkId, componentId, endpointId);
	}
	
	public void releaseLock(Long address, short linkId) {
		this.wkpf.sendReleaseLock(address, linkId);
	}
}
