package edu.uci.eecs.wukong.framework.gateway.rpc;

import java.util.Map;

public class TinyRPCRequest {
	private String jsonrpc;
	private String id;
	private String method;
	private Map<String, Object> params;
	
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
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	public TinyRPCResponse getSuccessResponse(Object result) {
		TinyRPCResponse response = new TinyRPCResponse(this.jsonrpc, this.id, null, result);
		return response;
	}
	
	public TinyRPCResponse getErrorResponse(RPCError err) {
		TinyRPCResponse response = new TinyRPCResponse(this.jsonrpc, this.id, err, null);
		return response;
	}
}
