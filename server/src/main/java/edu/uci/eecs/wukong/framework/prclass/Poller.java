package edu.uci.eecs.wukong.framework.prclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import edu.uci.eecs.wukong.prclass.ft.FaultTolerantExecutionExtenson;

public class Poller {
	private final static Logger LOGGER = LoggerFactory.getLogger(Poller.class);
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
		LOGGER.info("Send hearbeat message to node " + address);
		this.wkpf.sendGetDeviceStatus(address);
	}
	
	public void sendSetLock(Long address, short linkId) {
		this.wkpf.sendSetLock(address, linkId);
	}
	
	public void changeComponentMap(Long dest, short componentId, long oldNodeId, byte oldPid,
			long newNodeId, byte newPid) {
		LOGGER.info("Send change map message to node " + dest);
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
