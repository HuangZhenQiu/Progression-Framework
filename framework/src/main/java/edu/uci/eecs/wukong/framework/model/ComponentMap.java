package edu.uci.eecs.wukong.framework.model;

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
