package edu.uci.eecs.wukong.framework.gateway;

/**
 * ID request data format
 * 
 * @author peter
 *
 */
public class IDRequest {
	private long IFADDR;
	private int IFADDRLEN;
	private long IFNETMASK;
	private int PORT;
	private byte[] UUID;
	
	public IDRequest(long address, int length,
			long netmask, int port, byte[] uuid) {
		this.IFADDR = address;
		this.IFADDRLEN = length;
		this.IFNETMASK = netmask;
		this.PORT = port;
		this.UUID = uuid;
	}
}
