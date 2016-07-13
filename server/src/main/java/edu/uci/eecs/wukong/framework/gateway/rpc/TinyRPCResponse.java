package edu.uci.eecs.wukong.framework.gateway.rpc;

public class TinyRPCResponse {
	private String jsonrpc;
	private String id;
	private RPCError error;
	private Object result;
	
	public TinyRPCResponse(String jsonrpc, String id,
			RPCError error, Object result) {
		this.jsonrpc = jsonrpc;
		this.id = id;
		this.error = error;
		this.result = result;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RPCError getError() {
		return error;
	}

	public void setError(RPCError error) {
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
