package edu.uci.eecs.wukong.framework.gateway;

import java.net.SocketAddress;

import edu.uci.eecs.wukong.framework.device.DeviceManager;
import edu.uci.eecs.wukong.framework.mptn.MPTNPackage;
import edu.uci.eecs.wukong.framework.mptn.TCPMPTN;

public class Gateway implements RPCCommandHandler {
	private DeviceManager manager;
	private IDService idService;
	private TCPMPTN mptn;
	
	public Gateway(DeviceManager manager) {
		this.manager = manager;
		this.mptn = new TCPMPTN();
		this.idService = new IDService(this);
		this.mptn.register(idService);
	}
	
	public void dispatchWKPFMessage(SocketAddress remoteAddress, byte[] message) {
		
	}

	@Override
	public void onSend(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetDeviceType(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRouting(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDiscover(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdd(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDelete(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoll(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub
		
	}
}
