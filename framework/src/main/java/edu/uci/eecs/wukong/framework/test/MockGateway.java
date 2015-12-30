package edu.uci.eecs.wukong.framework.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.nio.NIOUdpServer;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.wkpf.MPTNMessageListener;
import edu.uci.eecs.wukong.framework.wkpf.MPTN;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
	private NIOUdpServer server;
	private PerformanceCollector collector;
	private MockReprogrammer programmer;
	private Map<LoadGenerator<?>, Long> generators;
	private Timer timer;
	private WKPFMessageSender sender; // init after server is added into network
	private int mastLength;
	private int mask;
	private byte nodeId;
	private long longAddress;
	
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
			this.mastLength = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
			this.mask = (2 ^ (mask + 1) -1) << (24 - mask);
		} catch (Exception e) {
			LOGGER.error("Can't get network mask for local host");
			System.exit(-1);
		}
	}

	@Override
	public void onMessage(ByteBuffer message) {
		byte head1 = message.get();
		byte head2 = message.get();
		
		if (head1 != 0xAA || head2 != 0x55) {
			LOGGER.error("Get unknow packet " + MPTNUtil.toHexString(message.array()));
			return;
		}
		
		byte hostId = message.get();
		message.order(ByteOrder.BIG_ENDIAN);
		int ip = message.getInt();
		short port = message.getShort();
		byte[] bytes = BigInteger.valueOf(ip).toByteArray();
		try {
			InetAddress address = InetAddress.getByAddress(bytes);
			this.nodeId = (byte) ((~mask) & ip);
			this.longAddress = ip;

			createWKPFMessageSender(address.getHostAddress(), port, nodeId, longAddress);

			byte type = message.get();
			int length = (int) message.get();
			if (type == MPTN.HEADER_TYPE_1) { 
				processMessage(message, length);
			} else if (type == MPTN.HEADER_TYPE_2){
				processInfoMessage(this.nodeId);
			} else {
				LOGGER.error("Received message error message type");
			}
		} catch (Exception e) {
			LOGGER.error("Can't create InetAddress from integer " + ip);
		}
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
		
	}
	
	/**
	 * Process the info request message
	 * 
	 * @param nodeId
	 */
	private void processInfoMessage(byte nodeId) {
		ByteBuffer buffer = ByteBuffer.allocate(1);
		buffer.put(nodeId);
		sender.send(nodeId, buffer.array());
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
