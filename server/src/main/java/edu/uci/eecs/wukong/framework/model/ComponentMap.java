package edu.uci.eecs.wukong.framework.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.model.Component;
import edu.uci.eecs.wukong.framework.model.EndPoint;

/**
 * It is a type of DJA file in Wukong. It contains all of the components in a FBP.
 */
public class ComponentMap {
	private static Logger logger = LoggerFactory.getLogger(ComponentMap.class);

	private List<Component> components;
	private Map<Short, List<Component>> componentMap;
	
	public ComponentMap() {
		this.components = new ArrayList<Component> ();
		this.componentMap = new HashMap<Short, List<Component>> ();
	}
	
	public byte[] toByteArray() {
		int length = length();
		ByteBuffer buffer = ByteBuffer.allocate(length);	
		buffer.put((byte) (components.size() % 256));
		buffer.put((byte) (components.size() / 256));
		
		int offset = 2 + 2 * components.size();
		for (Component component : components) {
			buffer.put((byte) (offset % 256));
			buffer.put((byte) (offset / 256));
			offset += component.length();	
		}
		
		for (Component component : components) {
			buffer.put(component.toByteArray());
		}
		
		return buffer.array();
	}
	
	public boolean updateComponent(int componentId, long oldNodeId, byte oldPid,
			long newNodeId, byte newPid) {
		if (componentId <= this.components.size()) {
			Component component = components.get(componentId);
			int i = 0;
			for (EndPoint point : component.getEndPoints()) {
				if (point.getNodeId() == oldNodeId && point.getPortId() == oldPid) {
					// Replace it with new one
					EndPoint old = point; 
					component.getEndPoints().set(i, new EndPoint(newNodeId, newPid));
					logger.info(String.format("Updated endpoint from %s to %s", old, component.getEndPoints().get(i)));
					return true;
				}
				i ++;
			}
		}
		
		logger.info(String.format("Can't find endpoint from for component id %d, wiht nodeId= %d and pid = %d",
				componentId, oldNodeId, oldPid));
		return false;
	}
	
	public int length() {
		int length = 2 + 2 * components.size();
		for (Component component : components) {
			length += component.length();
		}
		
		return length;
	}
	
	public List<Component> getReplicatedComponent() {
		List<Component> components = new ArrayList<Component> ();
		for (Component component : this.components) {
			if (component.getEndPointSize() > 1) {
				components.add(component); 
			}
		}
		
		return components;
	}
	
	public boolean contains(short wuClassId) {
		return componentMap.containsKey(wuClassId);
	}
	
	public Component getComponentById(int id) {
		if (id < components.size()) {
			return components.get(id);
		} else {
			return null;
		}
	}
	
	public List<Component> getComponentByWuClassId(short wuClassId) {
		return componentMap.get(wuClassId);
	}
	
	public void addComponent(Component component) {
		this.components.add(component);
		if (!componentMap.containsKey(component.getWuClassId())) {
			List<Component> comList = new ArrayList<Component> ();
			componentMap.put(component.getWuClassId(), comList);
		}
		this.componentMap.get(component.getWuClassId()).add(component);
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
