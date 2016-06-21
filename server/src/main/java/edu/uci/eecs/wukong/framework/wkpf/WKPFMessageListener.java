package edu.uci.eecs.wukong.framework.wkpf;

public interface WKPFMessageListener {
	
	public void onWKPFGetWuClassList(long sourceId, byte[] message);
	
	public void onWKPFGetWuObjectList(long sourceId, byte[] message);
	
	public void onWKPFReadProperty(long sourceId, byte[] message);
	
	public void onWKPFWriteProperty(long sourceId, byte[] message);
	
	public void onWKPFRequestPropertyInit(long sourceId, byte[] message);
	
	public void onWKPFMonitoredData(long sourceId, byte[] message);
	
	public void onWKPFSetLocation(long sourceId, byte[] message);
	
	public void onWKPFGetLocation(long sourceId, byte[] message);
	
	public void onWKPFRemoteProgramOpen(long sourceId, byte[] message);
	
	public void onWKPFRemoteProgramWrite(long sourceId, byte[] message);
	
	public void onWKPFRemoteProgramCommit(long sourceId, byte[] message);
	
	public void onWKPFLinkCounterReturn(long sourceId, byte[] message);
	
	public void onWKPFDeviceStatusReturn(long sourceId, byte[] message);
}
