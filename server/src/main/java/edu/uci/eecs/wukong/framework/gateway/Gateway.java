package edu.uci.eecs.wukong.framework.gateway;

import java.net.SocketAddress;

import edu.uci.eecs.wukong.framework.device.DeviceManager;
import edu.uci.eecs.wukong.framework.mptn.TCPMPTN;
import edu.uci.eecs.wukong.framework.mptn.packet.MPTNPacket;

public class Gateway implements RPCCommandHandler {
	private int id; // gateway ID
	private boolean enterLearnMode = false;
	private DeviceManager manager;
	private IDService idService;
	private TCPMPTN mptn;
	
	public Gateway(DeviceManager manager) {
		this.manager = manager;
		this.mptn = new TCPMPTN();
		this.idService = new IDService(this);
		this.mptn.register(idService);
	}
	
	public void dispatchWKPFMessage(SocketAddress remoteAddress, MPTNPacket message) {
		
	}
	
	public void dispatchRPCMessage(SocketAddress remoteAddress, MPTNPacket message) {
		
	}
	
	public TCPMPTN getMPTN() {
		return this.mptn;
	}

	@Override
	public void onGetDeviceType(SocketAddress remoteAddress, MPTNPacket bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRouting(SocketAddress remoteAddress, MPTNPacket bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDiscover(SocketAddress remoteAddress, MPTNPacket bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdd(SocketAddress remoteAddress, MPTNPacket bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDelete(SocketAddress remoteAddress, MPTNPacket bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop(SocketAddress remoteAddress, MPTNPacket bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoll(SocketAddress remoteAddress, MPTNPacket bytes) {
		// TODO Auto-generated method stub
		
	}
}
