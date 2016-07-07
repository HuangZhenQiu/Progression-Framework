package edu.uci.eecs.wukong.framework.model;

public class ChannelData<T> {
	private NPP npp;
	private WKPFMessageType type;
	private T value;
	
	public ChannelData(NPP npp, T value) {
		this.npp = npp;
		this.value = value;
	}
	
	public ChannelData(WKPFMessageType type, T value) {
		this.type = type;
		this.value = value;
	}

	public NPP getNpp() {
		return npp;
	}

	public void setNpp(NPP npp) {
		this.npp = npp;
	}

	public WKPFMessageType getType() {
		return type;
	}

	public void setType(WKPFMessageType type) {
		this.type = type;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
