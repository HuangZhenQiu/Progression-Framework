package edu.uci.eecs.wukong.framework.mptn;

public abstract class AbstractMPTN {
	public static int MPTN_HEADER_LENGTH = 9;
	public static byte HEADER_TYPE_1 = 1;
	public static byte HEADER_TYPE_2 = 2;
	
	public static int MPTN_ID_LEN = 4;
	public static int MPTN_MAX_ID = 2 ^ (MPTN_ID_LEN * 8) - 1;
	public static int MPTN_MSQTYPE_LEN = 1;
	
	public static int CONNECTION_RETRIES = 1;
	public static int NETWORK_TIMEOUT = 3;
	
	// transport interface sub network length
	public static int ZW_ADDRESS_LEN = 1;
	public static int ZB_ADDRESS_LEN = 2;
	public static int IP_ADDRESS_LEN = 4;
	
	// MODE for adding and deleting node
	public static int STOP_MODE = 0;
	public static int ADD_MODE = 1;
	public static int DEL_MODE = 2;
	
	// protocol handler admission control
	public static int ONLY_FROM_TCP_SERVER = 1; // Master or peer gateway
	public static int ONLY_FROM_TRANSPORT_INTERFACE = 2;
	public static int VALID_FROM_ALL = 3;
	
	// ID service
	public static final byte MPTN_MSGTYPE_GWDISCOVER = 0;
	public static final byte MPTN_MSGTYPE_GWOFFER = 1;
	public static final byte MPTN_MSGTYPE_IDREQ = 2;
	public static final byte MPTN_MSGTYPE_IDACK = 3;
	public static final byte MPTN_MSGTYPE_IDNAK = 4;
	public static final byte MPTN_MSGTYPE_GWIDREQ = 5;
	public static final byte MPTN_MSGTYPE_GWIDACK = 6;
	public static final byte MPTN_MSGTYPE_GWIDNAK = 7;
	
    // Heartbeat
	public static final byte MPTN_MSGTYPE_RTPING = 8;
	public static final byte MPTN_MSGTYPE_RTREQ = 9;
	public static final byte MPTN_MSGTYPE_RTREP = 10;
	
	// RPC service with master
	public static final byte MPTN_MSGTYPE_RPCCMD = 16;
	public static final byte MPTN_MSGTYPE_RPCREP = 17;
	
	// Message forward
	public static final byte MPTN_MSGTYPE_FWDREQ = 24;
	public static final byte MPTN_MSGTYPE_FWDACK = 25;
	public static final byte MPTN_MSGTYPE_FWDNAK = 26;
	
	public static final byte MPTN_ERROR = 99;
	
	public int MASTER_ID = 0;
	public int MPTN_UDP_PORT = 5775;
	
	public int GWIDREQ_PAYLOAD_LEN = 16;
	public int IDREQ_PAYLOAD_LEN = 16;
}
