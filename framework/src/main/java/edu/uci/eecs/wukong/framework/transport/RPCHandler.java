package edu.uci.eecs.wukong.framework.transport;

import java.util.Map;

public interface RPCHandler {

	public boolean send(String address, byte[] payload);
	
	public String getDeviceType();
	
	public Map<String, String[]> routing();
	
	public String[] discover();
	
	public boolean add();
	
	public boolean delete();
	
	public boolean stop();
	
	public String poll();
}
