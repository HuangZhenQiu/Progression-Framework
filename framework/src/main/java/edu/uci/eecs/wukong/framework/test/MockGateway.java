package edu.uci.eecs.wukong.framework.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.mptn.MPTN;
import edu.uci.eecs.wukong.framework.mptn.MPTNMessageListener;
import edu.uci.eecs.wukong.framework.nio.NIOUdpServer;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;

/**
 * Simulate the function of gateway to assign network Id to target progression server which is being tested.
 * The the meaning time receive the reply message, and calculate the metrics of server.
 */
public class MockGateway implements MPTNMessageListener {
	private final static Logger LOGGER = LoggerFactory.getLogger(MPTN.class);
	public final static long MOCK_GATEWAY_ADDRESS = 1;
	private NIOUdpServer server;
	private PerformanceCollector collector;
	private MockReprogrammer programmer;
	private Map<LoadGenerator<?>, Long> generators;
	private Timer timer;
	private WKPFMessageSender sender; // init after server is added into network
	private int maskLength;
	private int mask;
	private byte nodeId;
	private long longAddress;
	private byte[] longAddressBytes;
	
	public MockGateway(int port, MockReprogrammer programmer, PerformanceCollector collector) {
		this.server = new NIOUdpServer(port);
		this.generators = new HashMap<LoadGenerator<?>, Long> ();
		this.server.addMPTNMessageListener(this);
		this.timer = new Timer();
		this.collector = collector;
		this.programmer = programmer;
		
		try {
			InetAddress localHost = Inet4Address.getLocalHost();
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
			this.maskLength = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
			this.mask = (2 ^ (maskLength + 1) -1) << (24 - maskLength);
		} catch (Exception e) {
			LOGGER.error("Can't get network mask for local host");
			System.exit(-1);
		}
	}

	@Override
	public void onMessage(ByteBuffer message) {
		
		LOGGER.info("Get packet " + MPTNUtil.toHexString(message.array()));
		byte[] header = new byte[MPTN.MPTN_HEADER_LENGTH];
		message.get(header);
		int p1 = header[0] & 0xFF;
		int p2 = header[1] & 0xFF;
		if (p1 != 0xAA || p2 != 0x55) {
			LOGGER.error("Get unknow packet " + MPTNUtil.toHexString(message.array()));
			return;
		}
		int souceId = header[2];
		long ip = WKPFUtil.getLittleEndianInteger(header, 3);
		short port = WKPFUtil.getLittleEndianShort(header, 7);
		
		try {
			byte type = message.get();
			int length = (int) message.get();
			if (type == MPTN.HEADER_TYPE_2) {  // get local ID				
				InetAddress address = InetAddress.getByAddress(getIPBytes(header, 3));
				this.nodeId = (byte) ((~mask) & ip);
				this.longAddress = ip;
				setLongAddressBytes(longAddress);
				createWKPFMessageSender(address.getHostAddress(), port, nodeId, longAddress);
				processInfoMessage((byte) nodeId);
			} else if (type == MPTN.HEADER_TYPE_1){  // get Long ID
				processMessage(message, length);
			} else {
				LOGGER.error("Received message error message type");
			}
		} catch (Exception e) {
			LOGGER.error("Can't create InetAddress from integer :" + e);
		}
	}
	
	private byte[] getIPBytes(byte[] header, int start) {
		byte[] bytes = new byte[4];
		bytes[0] = header[start + 3];
		bytes[1] = header[start + 2];
		bytes[2] = header[start + 1];
		bytes[3] = header[start];
		return bytes;
	}
	
	private void setLongAddressBytes(long ip) {
		ByteBuffer buffer =  ByteBuffer.allocate(4);
		MPTNUtil.appendReversedInt(buffer, (int)ip);
		longAddressBytes = buffer.array();
	}
	
	//PerformanceCollector collector, String domain, int port, int nodeId, long longAddress
	private void createWKPFMessageSender(String host, short port, byte nodeId, long longAddress) {
		sender = new WKPFMessageSender(collector, host, port, nodeId, longAddress);
		for (LoadGenerator<?> generatoer : generators.keySet()) {
			generatoer.setSender(sender);
		}
	}
	
	/**
	 * Process the FWD and IDREQ Message
	 * 
	 * @param buffer
	 * @param length
	 */
	private void processMessage(ByteBuffer buffer, int length) {
		if (length > 9) {
			byte[] bytes = new byte[length];
			buffer.get(bytes);
			if (bytes[8] == MPTN.MPTN_MSGTYPE_IDREQ) {
				ByteBuffer payload = ByteBuffer.allocate(13);
				MPTNUtil.appendMPTNPacket(payload, MPTNUtil.MPTN_MASTER_ID, (int)this.longAddress, MPTNUtil.MPTN_MSQTYPE_IDREQ, this.longAddressBytes);
				sender.send((int)this.longAddress, MPTN.HEADER_TYPE_2, MPTN.MPTN_MSGTYPE_IDACK, payload.array());
			} else if (bytes[8] == MPTN.MPTN_MSQTYPE_FWDREQ) {
				
			} else {
				LOGGER.error("Wrong MPTN type, Mock gateway only accepts IDREQ and FWDREQ");
			}
		} else {
			LOGGER.error("Wrong MPTN message length, it is at least 9 bits long");
		}
	}
	
	/**
	 * Process the info request message
	 * 
	 * @param nodeId
	 */
	private void processInfoMessage(byte nodeId) {
		sender.sendNodeId((int)this.longAddress, nodeId);
	}
	
	public void schedule(LoadGenerator<?> generator, long period) {
		generator.setSender(sender);
		generators.put(generator, period);
	}
	
	public void generateLoad() {
		for (Entry<LoadGenerator<?>, Long> entry : generators.entrySet()) {
			timer.schedule(entry.getKey(), entry.getValue());
		}
	}
	
	public void start() {
		Thread serverThread = new Thread(server);
		serverThread.start();
	}
	
	public void shutdown() {
		server.shutdown();
	}
}
