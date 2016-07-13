package edu.uci.eecs.wukong.framework.gateway;

/**
 * ID request data format
 * 
 * @author peter
 *
 */
public class IDRequest {
	private String IFADDR;
	private int IFADDRLEN;
	private long IFNETMASK;
	private int PORT;
	private long UUID;
	
	public IDRequest(String address, int length,
			long netmask, int port, long uuid) {
		this.IFADDR = address;
		this.IFADDRLEN = length;
		this.IFNETMASK = netmask;
		this.PORT = port;
		this.UUID = uuid;
	}
}
