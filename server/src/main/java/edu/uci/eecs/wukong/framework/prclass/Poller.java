package edu.uci.eecs.wukong.framework.prclass;

import edu.uci.eecs.wukong.framework.wkpf.WKPF;

public class Poller {
	private WKPF wkpf;

	public Poller(WKPF wkpf) {
		this.wkpf = wkpf;
	}
	
	public int getNodeId() {
		return wkpf.getNetworkId();
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
	
	public void changeComponentMap(Long dest, short componentId, long oldNodeId, byte oldPid,
			long newNodeId, byte newPid) {
		this.wkpf.sendChangeComponentMap(dest, componentId, (int)oldNodeId, oldPid, (int)newNodeId, newPid);
	}
	
	public void changeLink(Long dest, short sourceId, byte sourcePid,
			short destId, byte destPid, short newSourceId, byte newSourcePid,
			short newDestId, byte newDestPid) {
		this.wkpf.sendChangeLink(dest, sourceId, sourcePid, destId,
				destPid, newSourceId, newSourcePid, newDestId, newDestPid);
	}
	
	public void releaseLock(Long address, short linkId) {
		this.wkpf.sendReleaseLock(address, linkId);
	}
}
