package edu.uci.eecs.wukong.framework.device;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.wkpf.WKPFMessageListener;

public class DeviceManager implements WKPFMessageListener {
	private List<Device> devices;
	
	public DeviceManager() {
		devices = new ArrayList<Device> ();
	}
	
	public void addDevice(Device device) {
		this.devices.add(device);
	}

	@Override
	public void onWKPFGetWuClassList(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFGetWuObjectList(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFReadProperty(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFWriteProperty(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFRequestPropertyInit(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFMonitoredData(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFSetLocation(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFGetLocation(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFRemoteProgramOpen(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFRemoteProgramWrite(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFRemoteProgramCommit(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFLinkCounterReturn(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFDeviceStatusReturn(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFSetLockReturn(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFChangeLinkReturn(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWKPFReleaseLockReturn(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		
	}
}
