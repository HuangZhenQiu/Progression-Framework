package edu.uci.eecs.wukong.framework.wkpf;

public interface WKPFMessageListener {
	
	public void onWKPFGetWuClassList(int sourceId, byte[] message);
	
	public void onWKPFGetWuObjectList(int sourceId, byte[] message);
	
	public void onWKPFReadProperty(int sourceId, byte[] message);
	
	public void onWKPFWriteProperty(int sourceId, byte[] message);
	
	public void onWKPFRequestPropertyInit(int sourceId, byte[] message);
	
	public void onWKPFMonitoredData(int sourceId, byte[] message);
	
	public void onWKPFSetLocation(int sourceId, byte[] message);
	
	public void onWKPFGetLocation(int sourceId, byte[] message);
	
	public void onWKPFRemoteProgramOpen(int sourceId, byte[] message);
	
	public void onWKPFRemoteProgramWrite(int sourceId, byte[] message);
	
	public void onWKPFRemoteProgramCommit(int sourceId, byte[] message);
}
