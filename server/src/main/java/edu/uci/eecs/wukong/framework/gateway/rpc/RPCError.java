package edu.uci.eecs.wukong.framework.gateway.rpc;

public class RPCError {
	private int code;
	private String message;
	
	public RPCError(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
