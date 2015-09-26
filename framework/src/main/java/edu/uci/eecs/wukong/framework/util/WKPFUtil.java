package edu.uci.eecs.wukong.framework.util;

public class WKPFUtil {
	public static final byte DEFAULT_OBJECT_SIZE = 4;
	public static final byte DEFAULT_CLASS_SIZE = 3;
	
	/* Virtual and Can create Instance */
	public static final byte PLUGIN_WUCLASS_TYPE = 3; // Need to confirm with Niels
	
	public static final byte WKPF_PROPERTY_TYPE_SHORT = 0;
	public static final byte WKPF_PROPERTY_TYPE_BOOLEAN = 1;
	public static final byte WKPF_PROPERTY_TYPE_REFRESH_RATE = 2;
	
	public static final byte WKPF_REPRG_OPEN                 = 0x10;
	public static final byte WKPF_REPRG_OPEN_R               = 0x11;
	public static final byte WKPF_REPRG_WRITE                = 0x12;
	public static final byte WKPF_REPRG_WRITE_R              = 0x13;
	public static final byte WKPF_REPRG_COMMIT               = 0x14;
	public static final byte WKPF_REPRG_COMMIT_R             = 0x15;
	
	public static final byte WKPF_REPROG_OK                  = 0x00;
	public static final byte WKPF_REPROG_REQUEST_RETRANSIMIT = 0x01;
	public static final byte WKPF_REPROG_TOOLARGE            = 0x02;
	public static final byte WKPF_REPROG_FAILED              = 0x03;
	
	
	
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
