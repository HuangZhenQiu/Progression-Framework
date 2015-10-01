package edu.uci.eecs.wukong.framework.model;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.model.EndPoint;

public class Component {
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
