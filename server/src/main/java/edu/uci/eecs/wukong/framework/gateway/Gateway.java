package edu.uci.eecs.wukong.framework.gateway;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import edu.uci.eecs.wukong.framework.device.DeviceManager;
import edu.uci.eecs.wukong.framework.gateway.rpc.RPCCommandHandler;
import edu.uci.eecs.wukong.framework.gateway.rpc.TinyRPCRequest;
import edu.uci.eecs.wukong.framework.gateway.rpc.TinyRPCResponse;
import edu.uci.eecs.wukong.framework.mptn.TCPMPTN;
import edu.uci.eecs.wukong.framework.mptn.packet.MPTNPacket;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;

public class Gateway implements RPCCommandHandler {
	private final static Logger LOGGER = LoggerFactory.getLogger(Gateway.class);
	private static Gson gson = new Gson();
	private int id; // gateway ID
	private boolean enterLearnMode = false;
	private DeviceManager manager;
	private IDService idService;
	private TCPMPTN mptn;
	private static final String[] RPC_METHOD =
		{"send", "getDeviceType", "routing", "discover", "add", "delete", "stop", "poll"};
	
	public Gateway(DeviceManager manager) {
		this.manager = manager;
		this.mptn = new TCPMPTN();
		this.idService = new IDService(this);
		this.mptn.register(idService);
	}
	
	public void dispatchWKPFMessage(SocketAddress remoteAddress, MPTNPacket message) {
		switch(message.getPayload()[0] & 0xFF) {
			case WKPFUtil.WKPF_REPRG_OPEN & 0xFF:
				manager.onWKPFRemoteProgramOpen(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_REPRG_WRITE & 0xFF:
				manager.onWKPFRemoteProgramWrite(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_REPRG_COMMIT & 0xFF:
				manager.onWKPFRemoteProgramCommit(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_GET_WUCLASS_LIST & 0xFF:
				manager.onWKPFGetWuClassList(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_GET_WUOBJECT_LIST & 0xFF:
				manager.onWKPFGetWuObjectList(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_READ_PROPERTY & 0xFF:
				manager.onWKPFReadProperty(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_WRITE_PROPERTY & 0xFF:
				manager.onWKPFWriteProperty(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_REQUEST_PROPERTY_INIT & 0xFF:
				manager.onWKPFRequestPropertyInit(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_GET_LOCATION & 0xFF:
				manager.onWKPFGetLocation(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_SET_LOCATION & 0xFF:
				manager.onWKPFSetLocation(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.MONITORING & 0xFF:
				manager.onWKPFMonitoredData(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_REPRG_REBOOT & 0xFF:
				LOGGER.debug("I dont't want to reboot");
				break;
			case WKPFUtil.WKPF_GET_LINK_COUNTER_R & 0xFF:
				manager.onWKPFLinkCounterReturn(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_GET_DEVICE_STATUS_R & 0xFF:
				manager.onWKPFDeviceStatusReturn(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_SET_LOCK_R:
				manager.onWKPFSetLockReturn(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_CHANGE_LINK_R:
				manager.onWKPFChangeLinkReturn(message.getSourceAddress(), message.getPayload());
				break;
			case WKPFUtil.WKPF_RELEASE_LOCK_R:
				manager.onWKPFReleaseLockReturn(message.getSourceAddress(), message.getPayload());
				break;
			default:
				LOGGER.error("Received unpexcted WKPF message type " + message.getPayload()[0]);
		}
	}
	
	public void dispatchRPCMessage(SocketAddress remoteAddress, MPTNPacket message) {
		TinyRPCRequest request = gson.fromJson(new String(message.getPayload()), TinyRPCRequest.class);
		TinyRPCResponse response = null;
		if (request.getMethod().equals(RPC_METHOD[0])) {
			response = this.onSend(remoteAddress, request);
		} else if (request.getMethod().equals(RPC_METHOD[1])) {
			response = this.onGetDeviceType(remoteAddress, request);
		} else if (request.getMethod().equals(RPC_METHOD[2])) {
			response = this.onRouting(remoteAddress, request);
		} else if (request.getMethod().equals(RPC_METHOD[3])) {
			response = this.onDiscover(remoteAddress, request);
		} else if (request.getMethod().equals(RPC_METHOD[4])) {
			response = this.onDelete(remoteAddress, request);
		} else if (request.getMethod().equals(RPC_METHOD[5])) {
			response = this.onPoll(remoteAddress, request);
		}
		
		byte[] payload = gson.toJson(response).getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(MPTNUtil.MPTN_HEADER_LENGTH + payload.length);
		this.mptn.send(MPTNUtil.MASTER_ID, buffer, false);
	}
	
	public TCPMPTN getMPTN() {
		return this.mptn;
	}

	@Override
	public TinyRPCResponse onGetDeviceType(SocketAddress remoteAddress, TinyRPCRequest request) {
		return request.getSuccessResponse(null);
	}

	@Override
	public TinyRPCResponse onRouting(SocketAddress remoteAddress, TinyRPCRequest request) {
		return request.getSuccessResponse(null);
	}

	@Override
	public TinyRPCResponse onDiscover(SocketAddress remoteAddress, TinyRPCRequest request) {
		return request.getSuccessResponse(null);
	}

	@Override
	public TinyRPCResponse onAdd(SocketAddress remoteAddress, TinyRPCRequest request) {
		return request.getSuccessResponse(null);
	}

	@Override
	public TinyRPCResponse onDelete(SocketAddress remoteAddress, TinyRPCRequest request) {
		return request.getSuccessResponse(null);	
	}

	@Override
	public TinyRPCResponse onStop(SocketAddress remoteAddress, TinyRPCRequest request) {
		return request.getSuccessResponse(null);
	}

	@Override
	public TinyRPCResponse onPoll(SocketAddress remoteAddress, TinyRPCRequest request) {
		return request.getSuccessResponse(null);	
	}

	@Override
	public TinyRPCResponse onSend(SocketAddress remoteAddress, TinyRPCRequest request) {
		int dest = (int)request.getParams().get("address");
		if (dest == 1) { // default progression server node Id
			byte[] payload = (byte[])request.getParams().get("payload");
			MPTNPacket packet = new MPTNPacket(payload);
			dispatchWKPFMessage(remoteAddress, packet);
		}
		
		return request.getSuccessResponse(null);
	}
}
