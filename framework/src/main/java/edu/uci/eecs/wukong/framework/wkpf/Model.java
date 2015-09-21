package edu.uci.eecs.wukong.framework.wkpf;

import java.util.Map;
import java.util.HashMap;

public class Model {

	public static class WuClass {
		private short wuclassId;
		private Map<Integer, String> properties;
		
		public WuClass(short wuclassId) {
			this.wuclassId = wuclassId;
			this.properties = new HashMap<Integer, String>();
		}
		
		public WuClass(short wuclassId, Map<Integer, String> properties) {
			this.wuclassId = wuclassId;
			this.properties = properties;
		}
		
		public void addProperty(Integer property, String name) {
			this.properties.put(property, name);
		}
		
		public short getWuClassId() {
			return wuclassId;
		}
	}
	
	public static class WuObject {
		private static byte sysport = 0;
		private WuClass type;
		private byte port;
		public WuObject(WuClass type) {
			this.port = sysport ++;
			this.type = type;
		}
		
		public byte getPort() {
			return this.port;
		}
		
		public WuClass getType() {
			return this.type;
		}
	}
	
	public static class LinkNode {
		private int nodeId;
		private short classId;
		private byte portId;
		private byte propertyId;
		
		public LinkNode(int nodeId, short classId, byte portId, byte propertyId) {
			this.nodeId = nodeId;
			this.classId = classId;
			this.portId = portId;
		}

		public int getNodeId() {
			return nodeId;
		}

		public void setNodeId(int nodeId) {
			this.nodeId = nodeId;
		}

		public short getClassId() {
			return classId;
		}

		public void setClassId(short classId) {
			this.classId = classId;
		}

		public byte getPortId() {
			return portId;
		}

		public void setPortId(byte portId) {
			this.portId = portId;
		}

		public byte getPropertyId() {
			return propertyId;
		}

		public void setPropertyId(byte propertyId) {
			this.propertyId = propertyId;
		}
	}
	
	public static class LinkTable {
		private Map<LinkNode, String> inLink;
		private Map<String, LinkNode> outLink;
		
		public LinkTable() {
			inLink = new HashMap<LinkNode, String>();  /* Physical key to property */
			outLink = new HashMap<String, LinkNode>(); /* Property to Physical key */
		}
		
		public void addInLink(LinkNode node, String property) {
			this.inLink.put(node, property);
		}
		
		public void addOutLink(LinkNode node, String property) {
			this.outLink.put(property, node);
		}
		
		public LinkNode getDestination(String property) {
			return this.outLink.get(property);
		}
	}
	
	public static class MonitorData {
		private int nodeId;
		private short portId;
		private double value;
		
		public MonitorData (int nodeId, short portId, double value) {
			this.nodeId = nodeId;
			this.portId = portId;
			this.value = value;
		}
		
		public Double getValue() {
			return value;
		}
	}
}
