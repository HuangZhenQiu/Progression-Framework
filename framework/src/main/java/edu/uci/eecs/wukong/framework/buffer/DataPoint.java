package edu.uci.eecs.wukong.framework.buffer;

import edu.uci.eecs.wukong.framework.model.NPP;

public class DataPoint<T> {
	private NPP npp;
	private int deviation;
	private T value;
	
	public DataPoint(NPP npp, int deviation, T value) {
		this.deviation = deviation;
		this.value = value;
	}
	
	public NPP getNPP() {
		return this.npp;
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