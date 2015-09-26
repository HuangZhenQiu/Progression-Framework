package edu.uci.eecs.wukong.framework.wkpf;

public interface WKPFMessageListener {
	
	public void onWKPFGetWuClassList(byte[] message);
	
	public void onWKPFGetWuObjectList(byte[] message);
	
	public void onWKPFReadProperty(byte[] message);
	
	public void onWKPFWriteProperty(byte[] message);
	
	public void onWKPFMonitoredData(byte[] message);
	
	public void onWKPFSetLocation(byte[] message);
	
	public void onWKPFGetLocation(byte[] message);
	
	public void onWKPFRemoteProgramOpen(byte[] message);
	
	public void onWKPFRemoteProgramWrite(byte[] message);
	
	public void onWKPFRemoteProgramCommit(byte[] message);
}
