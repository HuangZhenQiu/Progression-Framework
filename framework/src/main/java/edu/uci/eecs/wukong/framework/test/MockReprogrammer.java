package edu.uci.eecs.wukong.framework.test;

import edu.uci.eecs.wukong.framework.model.Component;
import edu.uci.eecs.wukong.framework.model.ComponentMap;
import edu.uci.eecs.wukong.framework.model.EndPoint;
import edu.uci.eecs.wukong.framework.model.InitValue;
import edu.uci.eecs.wukong.framework.model.InitValueTable;
import edu.uci.eecs.wukong.framework.model.Link;
import edu.uci.eecs.wukong.framework.model.LinkTable;

import edu.uci.eecs.wukong.framework.wkpf.DJAData.DJAConstants;

import java.nio.ByteBuffer;

/**
 * Simulate the reprogramming after mapping in master. It generate the mock link tables ß
 */
public class MockReprogrammer {
	private InitValueTable valueTable;
	private LinkTable linkTable;
	private ComponentMap map;
	
	public MockReprogrammer() {
		this.valueTable =  new InitValueTable();
		this.linkTable = new LinkTable();
		this.map = new ComponentMap();
	}
	
	public void addPrObject(short wuclassId, long address, byte port) {
		if (!map.contains(wuclassId)) {
			Component component = new Component(wuclassId);
			map.addComponent(component);
		}
		
		Component component = map.getComponent(wuclassId);
		component.addEndPoint(new EndPoint(address, port));
	}
	
	public void addLink(short sourceNodeId, byte spid, short destNodeId, byte dpid) {
		Link link = new Link(sourceNodeId, spid, destNodeId, dpid);
		linkTable.addLink(link);
	}
	
	public void addInitValue(short nodeId, byte pid, byte[] value) {
		InitValue v = new InitValue(nodeId, pid, value);
		valueTable.addInitValue(v);
	}
	
	public byte[] toByteArray() {
		int length = linkTable.length() + 3;
		length += valueTable.length() + 3;
		length += map.length() + 3;
		ByteBuffer buffer = ByteBuffer.allocate(length);
		
		appendFile(buffer, DJAConstants.DJ_FILETYPE_WKPF_COMPONENT_MAP, map.toByteArray());
		appendFile(buffer, DJAConstants.DJ_FILETYPE_WKPF_LINK_TABLE, linkTable.toByteArray());
		appendFile(buffer, DJAConstants.DJ_FILETYPE_WKPF_INITVALUES_TABLE, valueTable.toByteArray());
		
		return buffer.array();
	}
	
	private void appendFile(ByteBuffer buffer, byte type,  byte[] payload) {
		buffer.put((byte) (payload.length / 256));
		buffer.put((byte) (payload.length % 256));
		buffer.put(type);
		buffer.put(payload);
	}
}
