package edu.uci.eecs.wukong.framework.property;

public class Location {
	private float x;
	private float y;
	private float z;
	private int sequence;
	
	public Location(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	public int getSequence() {
		return this.sequence;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Location) {
			Location location = (Location) object;
			if (location.getX() == this.x && location.getY() == this.y && location.getZ() == this.z) {
				return true;
			}
		}
		
		return false;
	}
}
