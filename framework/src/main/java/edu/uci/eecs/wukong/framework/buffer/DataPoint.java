package edu.uci.eecs.wukong.framework.buffer;

public class DataPoint<T> {
	private int deviation;
	private T value;
	public DataPoint(int deviation, T value) {
		this.deviation = deviation;
		this.value = value;
	}
		
	public int getDeviation() {
		return deviation;
	}

	public void setDeviation(int deviation) {
		this.deviation = deviation;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public String toString() {
		return deviation + ":" + value;
	}
}