package edu.uci.eecs.wukong.framework.model;

public class ChannelData {
	private NPP npp;
	private short value;
	
	public ChannelData(NPP npp, short value) {
		this.npp = npp;
		this.value = value;
	}

	public NPP getNpp() {
		return npp;
	}

	public void setNpp(NPP npp) {
		this.npp = npp;
	}

	public short getValue() {
		return value;
	}

	public void setValue(short value) {
		this.value = value;
	}
}
