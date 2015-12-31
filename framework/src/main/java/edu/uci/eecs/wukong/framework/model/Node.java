package edu.uci.eecs.wukong.framework.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Class for node discovery. 
 * 
 */
public class Node {
	private int hostId;
	private long ip;
	private byte port;
	private List<Integer> wuclasses;
	private List<Integer> wuobjects;
	
	public Node(int hostId, long ip, byte port) {
		this.hostId = hostId;
		this.ip = ip;
		this.port = port;
		this.wuclasses = new ArrayList<Integer> ();
		this.wuobjects = new ArrayList<Integer> ();
	}

	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	public long getIp() {
		return ip;
	}
	public void setIp(long ip) {
		this.ip = ip;
	}
	public byte getPort() {
		return port;
	}
	public void setPort(byte port) {
		this.port = port;
	}
	public List<Integer> getWuclasses() {
		return wuclasses;
	}
	public void setWuclasses(List<Integer> wuclasses) {
		this.wuclasses = wuclasses;
	}
	public List<Integer> getWuobjects() {
		return wuobjects;
	}
	public void setWuobjects(List<Integer> wuobjects) {
		this.wuobjects = wuobjects;
	}
}
