package edu.uci.eecs.wukong.framework.gateway;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import edu.uci.eecs.wukong.framework.mptn.packet.MPTNPacket;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;

/**
 * Responsible for gateway Id exchange protocol handling. We don't consider status persistence
 * for now.
 * 
 * 
 * @author peter
 *
 */
public class IDService implements IDProtocolHandler {
	private final static Logger LOGGER = LoggerFactory.getLogger(IDService.class);
	private static Gson gson = new Gson();
	private Gateway gateway;
	private long gatewayId;
	private long netmask;
	private long address;
	private int addressLength;
	private byte[] uuid;
	// Need to check format
	private long network;
	private long idPrefix;
	// hash code of global routing table
	private byte[] rthash = null;
	
	public IDService(Gateway gateway) {
		this.gateway = gateway;
		this.addressLength = MPTNUtil.IP_ADDRESS_LEN;
		this.gatewayId = MPTNUtil.MPTN_MAX_ID.intValue();
		this.uuid = MPTNUtil.getUUIDBytes();
		try {
			InetAddress localHost = Inet4Address.getLocalHost();
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
			address = MPTNUtil.IPToInteger(networkInterface.getInterfaceAddresses().get(1).getAddress().getHostAddress());
			netmask = MPTNUtil.getNetMaskValue(
					networkInterface.getInterfaceAddresses().get(1).getNetworkPrefixLength());
		} catch (Exception e) {
			LOGGER.error("ID Service can't initialize network interface");
		}
	}
	
	public void getGatewayID() {
		long destId = MPTNUtil.MASTER_ID;
		long sourceId = MPTNUtil.MPTN_MAX_ID.intValue();
		byte messageType = MPTNUtil.MPTN_MSGTYPE_GWIDREQ;
		
		IDRequest request = new IDRequest(address, addressLength,
				netmask, Configuration.getInstance().getGatewayPort(), uuid);
		byte[] payload = gson.toJson(request).getBytes();		
		MPTNPacket requestPacket = new MPTNPacket(sourceId, destId, messageType, payload);
		MPTNPacket packet = gateway.getMPTN().send((int)destId, MPTNUtil.MASTER_ID, -1, requestPacket, true);
		if (packet == null) {
			LOGGER.error("GWIDREQ cannot get GWIDACK/NAK from master due to network problem");
		} else if (packet.getType() == MPTNUtil.MPTN_MSGTYPE_FWDNAK) {
			LOGGER.error("GWIDREQ GWIDREQ is refused by master");
		} else if (packet.getType() == MPTNUtil.MPTN_MSGTYPE_FWDACK) {
			LOGGER.error(String.format("GWIDREQ get unexpected msg type (%d) instead of GWIDACK/NAK from master", packet.getType()));
		} else if (packet.getDestAddress() != this.gatewayId) {
			destId = packet.getDestAddress();
			if (this.gatewayId == MPTNUtil.MPTN_MAX_ID.intValue()) {
				this.gatewayId = packet.getDestAddress();
				this.gateway.setId(packet.getDestAddress());
				// this.network =
				this.idPrefix = packet.getDestAddress() & netmask;
				LOGGER.info(String.format("GWIDREQ successuflly get new ID %s including prefix", destId));
			} else {
				LOGGER.error(String.format("GWIDREQ get an ID %d %s different from old one %d %s",
						destId, destId, this.gatewayId, this.gatewayId));
				System.exit(-1);
			}
		} else {
			LOGGER.info("GWIDREQ successfully check the ID %s with master", destId);
		}	
	}

	@Override
	public void onGatewayIDDisover(SocketAddress remoteAddress, long nouce, MPTNPacket packet) {
		LOGGER.info("Gateway ID discover request is not supported in progression server");
	}

	@Override
	public void onRoutingTablePing(SocketAddress remoteAddress, long nouce, MPTNPacket packet) {
		if (packet.getPayload() == null || packet.getPayload().length == 0) {
			LOGGER.error("RTPING should have the payload");
			return;
		}
		
		if (packet.getDestAddress() != this.gatewayId) {
			LOGGER.error("RTPING should be to the gateway");
			return;
		}
		
		if (packet.getSourceAddress() != MPTNUtil.MASTER_ID) {
			LOGGER.error(String.format("RTPING source ID %s should be Master 0", packet.getSourceAddress()));
			return;
		}
		
		if (rthash != packet.getPayload()) {
			rthash = packet.getPayload();
			
			// TODO (Peter Huang) currently we don't need to sync routing table
			
			
			/* LOGGER.debug(String.format("RTPING got different hash %s. mine is %s. need to update routing table", packet.getPayload(), rthash));
			ByteBuffer message = ByteBuffer.allocate(MPTNUtil.MPTN_HEADER_LENGTH);
			MPTNUtil.appendMPTNPackage(message, (int) gatewayId,
					MPTNUtil.MASTER_ID, MPTNUtil.MPTN_MSGTYPE_RTREQ, new byte[0]);
			gateway.getMPTN().send(MPTNUtil.MASTER_ID, message, false); */
		}
	}

	@Override
	public void onRoutingTableRequest(SocketAddress remoteAddress, long nouce, MPTNPacket packet) {
		LOGGER.info("Routing table request is not supported in progression server");

	}

	@Override
	public void onRountingTableReply(SocketAddress remoteAddress, long nouce, MPTNPacket packet) {
		if (packet.getPayload() == null || packet.getPayload().length == 0) {
			LOGGER.error("Routing table reply should have the payload");
			return;
		}
		
		if (packet.getSourceAddress() != MPTNUtil.MASTER_ID) {
			LOGGER.error(String.format("invalid RTREP message src ID %s: only support forward message from master"),
					packet.getSourceAddress());
			return;
		}
		
		// It is packet to progression server 
		if (packet.getDestAddress() != this.gatewayId) {
			LOGGER.error("Routing table reply dest id should me");
			return;
		}
		// gson.fromJson(new String(packet.getPayload()), classOfT);
	}

	@Override
	public void onForwardRequest(SocketAddress remoteAddress, long nouce, MPTNPacket packet) {
		if (packet.getPayload() == null || packet.getPayload().length == 0) {
			LOGGER.error("FWDREQ should have the payload");
			return;
		}
		
		if (packet.getSourceAddress() != MPTNUtil.MASTER_ID) {
			LOGGER.error(String.format("invalid FWDREQ src ID %s: only support forward message from master"),
					packet.getSourceAddress());
			return;
		}
		
		// It is packet to progression server 
		if (packet.getDestAddress() == MPTNUtil.MPTN_PROGRESSION_SERVER) {
			gateway.dispatchWKPFMessage(remoteAddress, packet);
		}
	}

	@Override
	public void onGatewayIDRequest(SocketAddress remoteAddress, long nouce, MPTNPacket packet) {
		LOGGER.info("ID request is not supported in progression server");
	}

	@Override
	public void onRPCCommand(SocketAddress remoteAddress, long nouce, MPTNPacket packet) {
		if (packet.getPayload() == null || packet.getPayload().length == 0) {
			LOGGER.error("RPCCMD should have the payload");
			return;
		}
		
		if (packet.getSourceAddress() != MPTNUtil.MASTER_ID) {
			LOGGER.error(String.format("invalid RPCCMD src ID %s: only support forward message from master"),
					packet.getSourceAddress());
			return;
		}
		
		gateway.dispatchRPCMessage(remoteAddress, nouce, packet);
	}
}
