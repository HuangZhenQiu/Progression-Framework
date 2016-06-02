package edu.uci.eecs.wukong.framework.transport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.jni.ZwaveJNI;
import edu.uci.eecs.wukong.framework.mptn.MPTN;

public class ZWTransport extends Transport {
	private static Logger logger = LoggerFactory.getLogger(ZWTransport.class);
	private ZwaveJNI zwave;
	private int networkId = 0;
	private int nodeId = 0;
	public ZWTransport(String deviceAddress, String name) {
		super(deviceAddress, name);
		zwave = new ZwaveJNI();
		try {
			zwave.setVerbose(0);
			zwave.setDebug(0);
		} catch (Exception e) {
			logger.error("PyZwave module has been updated. Please RE-INSTALL the zwave module in the native folder");
			logger.error("Using command: build ZwaveJNI");
			System.exit(-1);
		}
		
		zwave.init(deviceAddress);
		
		try {
			String address = zwave.getAddr();
			String b = address.substring(0, 5);
			for (int i = 0; i < b.length(); i++) {
				networkId += b.charAt(i) << (b.length() - i);
			}
			this.nodeId = b.charAt(4);
		} catch (Exception e) {
			logger.error("PyZwave module has been updated. Please RE-INSTALL the zwave module in the native folder");
			logger.error("Using command: build ZwaveJNI");
			System.exit(-1);
		}
		
		logger.info(String.format("Transport interface %s initilized on %s with network ID %s and Node ID %s ", name, deviceAddress, networkId, nodeId));
	}
	
	@Override
	public synchronized Object[] receive(int waitmsecond) {
		try {
			Object[] reply = zwave.receive(waitmsecond);
			if (reply.length == 2) {
				logger.info(String.format("recevies message %s from address %s", reply[1], reply[0]));
				return reply;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("receives exception %s", e.toString()));
		}
		
		Object[] ret = new Object[2];
		return ret;
	}
	
	@Override
	public synchronized boolean sendRaw(String address, byte[] payload) {
		try {
			logger.info(String.format("sending %d bytes %s to %s", payload.length, payload, address));
			zwave.send(address, payload);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("send raw exception %s", e.toString()));
		}
		
		return false;
	}

	@Override
	public boolean send(String address, byte[] payload) {
		boolean result = this.sendRaw(address, payload);
		if (!result) {
			logger.error(String.format("%s fails to send to address %s", name, address));
		}
		
		return result;
	}

	@Override
	public synchronized String getDeviceType() {
		String ret = null;
		try {
			ret = zwave.getAddr();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getDeviceType exception %s", e.toString());
		}
		return ret;
	}
	
	private synchronized String[] getNodeRoutingInfo(String address) {
		try {
			return zwave.routing(address);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getNodeRoutingInfo for address %s exception %s", address, e.toString());
		}
		return null;
	}

	@Override
	public Map<String, String[]> routing() {
		Map<String, String[]> routingTable = new HashMap<String, String[]> ();
		for (String nodeId : this.discover()) {
			String[] routTable = getNodeRoutingInfo(nodeId);
			if (routTable != null) {
				routingTable.put(nodeId, routTable);
			}
		}
		
		return routingTable;
	}

	public String[] nativeDiscover() {
		String[] ret = null;
		if (!this.stop()) {
			logger.error("cannot discover without STOP mode");
			return ret;
		}

		String[] nodes = zwave.discover();
		String zwaveController = nodes[0];
		String totalNodes = nodes[1];
		ret = Arrays.copyOfRange(nodes, 2, nodes.length);
		logger.info(String.format("zwave_controller: %s totalNodes: %s, ret: $s", zwaveController, totalNodes, ret));
		// In case zwave controller is in the list
		ArrayUtils.removeElement(ret, zwaveController);
		return ret;
	}

	@Override
	public synchronized boolean add() {
		boolean ret = false;
		try {
			zwave.add();
			this.mode = Mode.ADD;
			ret = true;
		} catch (Exception e) {
			logger.error(String.format("Fail to be ADD mode, now in %s node error %s", mode, e.toString()));
		}
		
		return ret;
	}

	@Override
	public synchronized boolean delete() {
		boolean ret = false;
		try {
			zwave.delete();
			this.mode = Mode.DEL;
			ret = true;
		} catch (Exception e) {
			logger.error(String.format("fails to be DEL mode, now in %s mode error: ", mode, e.toString()));
		}

		return ret;
	}

	@Override
	public synchronized boolean stop() {
		boolean ret = false;
		try {
			zwave.stop();
			this.mode = Mode.STOP;
			ret = true;
		} catch (Exception e) {
			logger.error(String.format("fails to be DEL mode, now in %s mode error: %s", mode, e.toString()));
		}
		return ret;
	}

	@Override
	public synchronized String poll() {
		String ret = "";
		try {
			ret = zwave.poll();
			ret.replace('\n', ' ');
		} catch (Exception e) {
			logger.error(String.format("fails to poll status from %s error: %s", name, e.toString()));
		}
		
		return ret;
	}

	@Override
	public int getAddressLength() {
		return MPTN.ZW_ADDRESS_LEN;
	}

	@Override
	public Mode getLearningMode() {
		return this.mode;
	}
}
