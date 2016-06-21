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
}
