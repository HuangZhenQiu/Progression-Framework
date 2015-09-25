package edu.uci.eecs.wukong.framework.wkpf;

import java.util.Map;
import java.util.HashMap;

public class Model {
	
	/**
	 * Network Port Property (NPP) represents an unique stream in WuKong system.
	 */
	public static class NPP {
		/* Network ID*/
		private int nid;
		/* Represent a particular WuObject in the server*/
		private byte portId;
		/* Represent a particular WuProperty of a WuClass*/
		private byte propertyId;
		
		public NPP(int nid, byte portId, byte propertyId) {
			this.nid = nid;
			this.portId = portId;
			this.propertyId = propertyId;
		}
	}

	public static class WuClassModel {
		private short wuclassId;
		private Map<Integer, String> properties;
		
		public WuClassModel(short wuclassId) {
			this.wuclassId = wuclassId;
			this.properties = new HashMap<Integer, String>();
		}
		
		public WuClassModel(short wuclassId, Map<Integer, String> properties) {
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
	
	public static class WuObjectModel {
		private static byte sysport = 0;
		private WuClassModel type;
		private byte port;
		public WuObjectModel(WuClassModel type) {
			this.port = sysport ++;
			this.type = type;
		}
		
		public byte getPort() {
			return this.port;
		}
		
		public WuClassModel getType() {
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
	
	public static class ComponentMap {
		
	}
	
	public static class Link {
		/* Component Id*/
		private int sourceId;
		private byte sourcePortId;
		/* Component Id*/
		private int destId;
		private byte destPortId;
		
		public Link(int sourceId, byte sourcePortId, int destId, byte destPortId) {
			this.sourceId = sourceId;
			this.sourcePortId = sourcePortId;
			this.destId = destId;
			this.destPortId = destPortId;
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
