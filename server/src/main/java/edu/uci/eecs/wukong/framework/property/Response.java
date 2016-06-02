package edu.uci.eecs.wukong.framework.property;

public class Response {
	private int sequence;
	
	public Response(int sequence) {
		this.sequence = sequence;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	public String toString() {
		return "Response[sequence = " + sequence + "]";
	}
}
