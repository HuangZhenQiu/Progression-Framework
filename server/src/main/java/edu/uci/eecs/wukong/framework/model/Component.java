package edu.uci.eecs.wukong.framework.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.model.EndPoint;

/**
 * A component presents a node in wukong Flow Based Program. The Component class defines a unit in component map,
 * which is sent by master for reprogramming. A component could have multiple end-points, because it can be mapped
 * to multiple nodes for the purpose of fault tolerant. 
 *
 */
public class Component {
	private short wuclassId;
	private short id;
	private List<EndPoint> endPoints;
	
	public Component(short wuclassId, short id) {
		this.wuclassId = wuclassId;
		this.id = id;
		this.endPoints = new ArrayList<EndPoint> ();
	}
	
	public byte[] toByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(length());
		buffer.put((byte) (endPoints.size() % 256));
		buffer.put((byte) (wuclassId % 256));
		buffer.put((byte) (wuclassId / 256));
		for (EndPoint point : endPoints) {
			buffer.put(point.toByteArray());
		}
		
		return buffer.array();
	}
	
	public int length() {
		return 3 + EndPoint.ENDPOINT_LENGTH * endPoints.size();
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
			return endPoints.get(0);
		}
		
		return null;
	}
	
	public EndPoint getSecondaryEndPoint() {
		if (endPoints.size() >= 2) {
			return endPoints.get(1);
		}
		
		return null;
	}
	
	public short getId() {
		return this.id;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Component) {
			Component obj = (Component) object;
			if (this.wuclassId == obj.getWuClassId()
					&& this.endPoints.equals(obj.endPoints)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return wuclassId + endPoints.hashCode();
	}
	
	@Override
	public String toString() {
		return "Component[WuClassId = " + wuclassId + ", EndPoints = " + endPoints.toString() + "]";
	}
}
