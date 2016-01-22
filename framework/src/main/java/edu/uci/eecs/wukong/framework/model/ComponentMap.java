package edu.uci.eecs.wukong.framework.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.model.Component;
import edu.uci.eecs.wukong.framework.model.EndPoint;

/**
 * It is a type of DJA file in Wukong. It contains all of the components in a FBP.
 */
public class ComponentMap {
	private List<Component> components;
	private Map<Short, Component> componentMap;
	
	public ComponentMap() {
		this.components = new ArrayList<Component> ();
		this.componentMap = new HashMap<Short, Component> ();
	}
	
	public byte[] toByteArray() {
		int length = length();
		ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.put((byte) (length % 256));
		buffer.put((byte) (length / 256));
		for (Component component : components) {
			buffer.put(component.toByteArray());
		}
		
		return buffer.array();
	}
	
	public int length() {
		int length = 2;
		for (Component component : components) {
			length += component.length();
		}
		
		return length;
	}
	
	public boolean contains(short wuClassId) {
		return componentMap.containsKey(wuClassId);
	}
	
	public Component getComponent(short wuClassId) {
		return componentMap.get(wuClassId);
	}
	
	public void addComponent(Component component) {
		if (!componentMap.containsKey(component.getWuClassId())) {
			this.components.add(component);
			this.componentMap.put(component.getWuClassId(), component);
		}
	}
	
	public long getPrimaryEndPointNodeId(int componentId) {
		if (componentId < components.size()) {
			return components.get(componentId).getPrimaryEndPoint().getNodeId();
		}
		
		return -1;
	}
	
	public byte getPrimaryEndPointPortId(int componentId) {
		if (componentId < components.size())  {
			return components.get(componentId).getPrimaryEndPoint().getPortId();
		}
		
		return -1;
	}
	
	/**
	 * Find the component id for the port
	 * @return
	 */
	public int getComponentId(byte portId, long nodeId) {
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
			return components.get(componentId).getWuClassId();
		}
		
		return -1;
	}
	
	/**
	 * Find the WuClasses that need to create new object instance 
	 * @param nodeId
	 * @return Map from port to wuclass Id
	 */
	public Map<Byte, Short> getWuClassIdList(long nodeId) {
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
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof ComponentMap) {
			ComponentMap map = (ComponentMap) object;
			return this.components.equals(map.components);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.components.hashCode();
	}

	@Override
	public String toString() {
		return components.toString();
	}
}
