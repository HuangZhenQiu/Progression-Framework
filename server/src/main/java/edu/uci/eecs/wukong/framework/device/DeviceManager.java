package edu.uci.eecs.wukong.framework.device;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.gateway.WKPFRPCHandler;
import edu.uci.eecs.wukong.framework.model.TCPMPTNPackage;

public class DeviceManager implements WKPFRPCHandler {
	private List<Device> devices;
	
	public DeviceManager() {
		devices = new ArrayList<Device> ();
	}
	
	public void addDevice(Device device) {
		this.devices.add(device);
	}
	

	@Override
	public void onSend(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetDeviceType(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRouting(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDiscover(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAdd(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDelete(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPoll(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}
}
