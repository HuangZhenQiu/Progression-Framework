package edu.uci.eecs.wukong.framework.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.model.MPTNPackage;
import edu.uci.eecs.wukong.framework.model.WKPFPackage;
import edu.uci.eecs.wukong.framework.mptn.MPTN;
import edu.uci.eecs.wukong.framework.mptn.MPTNMessageListener;
import edu.uci.eecs.wukong.framework.nio.NIOUdpServer;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
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
	private DeadlineIntensiveFBPBuilder builder;
	private Map<LoadGenerator<?>, Long> generators;
	private Timer timer;
	private WKPFMessageSender sender; // init after server is added into network
	private int maskLength;
	private int mask;
	private byte nodeId;
	private int longAddress;
	private byte[] longAddressBytes;
	
	public MockGateway(int port, MockReprogrammer programmer, PerformanceCollector collector) {
		this.server = new NIOUdpServer(port);
		this.generators = new HashMap<LoadGenerator<?>, Long> ();
		this.server.addMPTNMessageListener(this);
		this.timer = new Timer();
		this.collector = collector;
		this.programmer = programmer;
		this.builder = new DeadlineIntensiveFBPBuilder(programmer);
		
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
	public void onMessage(MPTNPackage mptnPackage) {
		try {
			LOGGER.info("Get packet " + MPTNUtil.toHexString(mptnPackage.getPayload()));
					
			if (mptnPackage.getH1() != 0xAA ||  mptnPackage.getH2() != 0x55) {
				LOGGER.error("Get unknow MPTN packet");
				return;
			}
			
			if (mptnPackage.getType() == MPTN.HEADER_TYPE_2) {  // get local ID				
				InetAddress address = InetAddress.getByAddress(mptnPackage.getSourceIPBytes());
				this.nodeId = (byte) ((~mask) & mptnPackage.getSourceIP());
				this.longAddress = mptnPackage.getSourceIP();
				setLongAddressBytes(longAddress);
				createWKPFMessageSender(address.getHostAddress(), mptnPackage.getSoucePort(), nodeId, longAddress);
				processInfoMessage((byte) nodeId);
			} else if (mptnPackage.getType() == MPTN.HEADER_TYPE_1){  // get Long ID
				processMessage(mptnPackage);
			} else {
				LOGGER.error("Received message error message type");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Unexpected exception when handle mptn message :" + e.getMessage());
		}
	}

	
	private void setLongAddressBytes(long ip) {
		ByteBuffer buffer =  ByteBuffer.allocate(4);
		MPTNUtil.appendReversedInt(buffer, (int)ip);
		longAddressBytes = buffer.array();
	}
	
	//PerformanceCollector collector, String domain, int port, int nodeId, long longAddress
	private void createWKPFMessageSender(String host, short port, byte nodeId, int longAddress) {
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
	 * @throws InterruptedException 
	 */
	private void processMessage(MPTNPackage mptnPackage) throws InterruptedException {
		if (mptnPackage.getLength() > 9) {
			WKPFPackage pack = new WKPFPackage(mptnPackage.getPayload());
			
			/* receive async response*/
			if(sender != null && sender.updatePackage(pack)) {
				return;
			}
		
			if (pack.getType() == MPTN.MPTN_MSGTYPE_IDREQ) {
				ByteBuffer payload = ByteBuffer.allocate(4);
				payload.putInt(longAddress);
				sender.send(MPTN.HEADER_TYPE_2, MPTN.MPTN_MSGTYPE_IDACK, payload.array());
				Thread.sleep(3000);
				/* start to generate mock FBP */
				byte[] infusion = builder.build();
				sender.reprogram(infusion);
				
				/* Schedule load generators  */
				// schedule();
				// startLoad();
			} else if (pack.getType() == MPTN.MPTN_MSQTYPE_FWDREQ) {
				/* Collect performance data */
				
				
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
	
	public void schedule() {
		List<LoadGenerator<?>> loads = builder.createLoadGenerator();
		for (LoadGenerator<?> generator : loads) {
			generator.setSender(sender);
			generators.put(generator, 5L);
		}
	}
	
	public void startLoad() {
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
