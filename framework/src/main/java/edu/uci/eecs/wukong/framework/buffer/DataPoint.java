package edu.uci.eecs.wukong.framework.buffer;

public class DataPoint<T> {
	private int deviation;
	private T value;
	public DataPoint(int deviation, T value) {
		this.deviation = deviation;
		this.value = value;
	}
	
	public String toString() {
		return deviation + ":" + value;
	}
}