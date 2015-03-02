package edu.uci.eecs.wukong.framework.buffer;

public class DataPoint {
	private int deviation;
	private short value;
	public DataPoint(int deviation, short value) {
		this.deviation = deviation;
		this.value = value;
	}
	
	public String toString() {
		return deviation + ":" + value;
	}
}