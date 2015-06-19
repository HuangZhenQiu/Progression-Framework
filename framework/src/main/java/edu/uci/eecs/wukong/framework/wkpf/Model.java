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
	}
	
	public static class WuObject {
		private WuClass type;
		private int port;
		public WuObject(WuClass type, int port) {
			this.port = port;
			this.type = type;
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
}
