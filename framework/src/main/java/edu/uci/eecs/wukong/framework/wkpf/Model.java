package edu.uci.eecs.wukong.framework.wkpf;

import java.util.ArrayList;
import java.util.List;
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
		private Map<String, Integer> properties;
		
		public WuClassModel(short wuclassId) {
			this.wuclassId = wuclassId;
			this.properties = new HashMap<String, Integer>();
		}
		
		public WuClassModel(short wuclassId, Map<String, Integer> properties) {
			this.wuclassId = wuclassId;
			this.properties = properties;
		}
		
		public int getPropertyId(String property) {
			if (properties.containsKey(property)) {
				return properties.get(property);
			}
			
			return -1;
		}
		
		public void addProperty(String name, Integer propertyId) {
			this.properties.put(name, propertyId);
		}
		
		public short getWuClassId() {
			return wuclassId;
		}
	}
	
	public static class WuObjectModel {
		private WuClassModel type;
		private int pluginId;
		private byte port;
		public WuObjectModel(WuClassModel type, byte port, int pluginId) {
			this.port = port;
			this.type = type;
			this.pluginId = pluginId;
		}
		
		public byte getPropertyId(String property) {
			if (type != null) {
				type.getPropertyId(property);
			}
			
			return -1;
		}
		
		public byte getPort() {
			return this.port;
		}
		
		public WuClassModel getType() {
			return this.type;
		}
		
		public int getPluginId() {
			return this.pluginId;
		}
	}
	
	public static class EndPoint {
		/* Network address */
		private int nodeId;
		private byte portId;
		
		public EndPoint(int nodeId, byte portId) {
			this.nodeId = nodeId;
			this.portId = portId;
		}
		
		public int getNodeId() {
			return nodeId;
		}
		
		public byte getPortId() {
			return portId;
		}
	}

	public static class Component {
		private short wuclassId;
		private List<EndPoint> endPoints;
		
		public Component(short wuclassId) {
			this.wuclassId = wuclassId;
			this.endPoints = new ArrayList<EndPoint> ();
		}
		
		public void addEndPoint(EndPoint endpoint) {
			endPoints.add(endpoint);
		}
		
		public short getWuClassId() {
			return this.wuclassId;
		}
		
		public int getEndPointSize() {
			return this.endPoints.size();
		}
		
		public EndPoint getEndPoint(int i) {
			if (i < getEndPointSize()) {
				return this.endPoints.get(i);
			}
			
			return null;
		}
		
		public EndPoint getPrimaryEndPoint() {
			if (endPoints.size() > 0) {
				endPoints.get(0);
			}
			
			return null;
		}
	}
	
	public static class ComponentMap {
		private List<Component> components;
		public ComponentMap() {
			this.components = new ArrayList<Component> ();
		}
		
		public void addComponent(Component component) {
			this.components.add(component);
		}
		
		public int getPrimaryEndPointNodeId(int componentId) {
			if (componentId < components.size()) {
				components.get(componentId).getPrimaryEndPoint().getNodeId();
			}
			
			return -1;
		}
		
		public byte getPrimaryEndPointPortId(int componentId) {
			if (componentId < components.size())  {
				components.get(componentId).getPrimaryEndPoint().getPortId();
			}
			
			return -1;
		}
		
		/**
		 * Find the component id for the port
		 * @return
		 */
		public int getComponentId(byte portId, int nodeId) {
			for (int i=0; i < components.size(); i++) {
				for (int j=0; j < components.get(i).getEndPointSize(); j++) {
					if (components.get(i).getEndPoint(j).getNodeId() == nodeId &&
							components.get(i).getEndPoint(j).getPortId() == portId) {
						return i; // find right component id
					}
				}
			}
			
			return -1;
		}
		
		/**
		 * Get wuclass Id for a component id
		 */
		public short getWuClassId(int componentId) {
			if (componentId < components.size()) {
				components.get(componentId).getWuClassId();
			}
			
			return -1;
		}
		
		/**
		 * Find the WuClasses that need to create new object instance 
		 * @param nodeId
		 * @return Map from port to wuclass Id
		 */
		public Map<Byte, Short> getWuClassIdList(Integer nodeId) {
			Map<Byte, Short> wuclassMap = new HashMap<Byte, Short>();
			for (Component component : components) {
				EndPoint primary = component.getPrimaryEndPoint();
				if (primary != null) {
					if (primary.getNodeId() == nodeId) {
						wuclassMap.put(primary.getPortId(), component.getWuClassId());
					}
				}
			}
			
			return wuclassMap;
		}
	}
	
	public static class Link {
		/* Component Id */
		private int sourceId;
		/* Property Id */
		private byte sourcePid;
		/* Component Id */
		private int destId;
		/* Property Id */
		private byte destPid;
		
		public Link(int sourceId, byte sourcePid, int destId, byte destPid) {
			this.sourceId = sourceId;
			this.sourcePid = sourcePid;
			this.destId = destId;
			this.destPid = destPid;
		}

		public int getSourceId() {
			return sourceId;
		}

		public void setSourceId(int sourceId) {
			this.sourceId = sourceId;
		}

		public byte getSourcePid() {
			return sourcePid;
		}

		public void setSourcePid(byte sourcePid) {
			this.sourcePid = sourcePid;
		}

		public int getDestId() {
			return destId;
		}

		public void setDestId(int destId) {
			this.destId = destId;
		}

		public byte getDestPid() {
			return destPid;
		}

		public void setDestPid(byte destPid) {
			this.destPid = destPid;
		}
	}
	
	public static class LinkTable {
		private List<Link> links;
		public LinkTable() {
			links = new ArrayList<Link> ();
		}
		
		public void addLink(Link link) {
			this.links.add(link);
		}
		
		public List<Link> getOutLinks(int srcId, byte propertyId) {
			List<Link> outLinks = new ArrayList<Link> ();
			for (Link link : links) {
				if (link.getSourceId() == srcId && link.getSourcePid() == propertyId) {
					outLinks.add(link);
				}
			}
			
			return outLinks;
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
