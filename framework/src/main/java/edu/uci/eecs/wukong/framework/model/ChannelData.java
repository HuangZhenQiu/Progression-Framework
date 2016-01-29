package edu.uci.eecs.wukong.framework.model;

public class ChannelData<T> {
	private NPP npp;
	private T value;
	
	public ChannelData(NPP npp, T value) {
		this.npp = npp;
		this.value = value;
	}

	public NPP getNpp() {
		return npp;
	}

	public void setNpp(NPP npp) {
		this.npp = npp;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
