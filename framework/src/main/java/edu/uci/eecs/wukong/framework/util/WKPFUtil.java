package edu.uci.eecs.wukong.framework.util;

public class WKPFUtil {
	public static final byte REPRG_OPEN                   = 0x10;
	public static final byte REPRG_OPEN_R                 = 0x11;
	public static final byte REPRG_WRITE                  = 0x20;
	public static final byte REPRG_WRITE_R_OK             = 0x21;
	public static final byte REPRG_WRITE_R_RETRANSMIT     = 0x22;
	public static final byte REPRG_COMMIT                 = 0x30;
	public static final byte REPRG_COMMIT_R_OK            = 0x31;
	public static final byte REPRG_COMMIT_R_RETRANSMIT    = 0x32;
	public static final byte REPRG_COMMIT_R_FAILED        = 0x33;
	
	public static final byte WKPF_GET_WUCLASS_LIST        = (byte)0x90;
	public static final byte WKPF_GET_WUCLASS_LIST_R      = (byte)0x91;
	public static final byte WKPF_GET_WUOBJECT_LIST       = (byte)0x92;
	public static final byte WKPF_GET_WUOBJECT_LIST_R     = (byte)0x93;
	public static final byte WKPF_READ_PROPERTY	        = (byte)0x94;
	public static final byte WKPF_READ_PROPERTY_R         = (byte)0x95;
	public static final byte WKPF_WRITE_PROPERTY	        = (byte)0x96;
	public static final byte WKPF_WRITE_PROPERTY_R	    = (byte)0x97;
	public static final byte WKPF_GET_LOCATION            = (byte)0x9A;
	public static final byte WKPF_GET_LOCATION_R          = (byte)0x9B;
	public static final byte WKPF_SET_LOCATION            = (byte)0x9C;
	public static final byte WKPF_SET_LOCATION_R          = (byte)0x9D;
	
	public static final byte MONITORING                   = (byte)0xB5;
}
