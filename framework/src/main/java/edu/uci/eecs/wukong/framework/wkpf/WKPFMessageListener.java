package edu.uci.eecs.wukong.framework.wkpf;

import java.util.List;

public interface WKPFMessageListener {

	public boolean onWKPFRemoteProgram(byte[] message);
	
	public List<String> onWKPFGetWuClassList();
	
	public List<String> onWKPFGetWuObjectList();
	
	public int onWKPFReadProperty(int objectId, int portId);
	
	public int onWKPFWriteProperty(int objectId, int portId);
	
	public boolean onWKPFMonitoredData(int deviceId, int portId);
}
