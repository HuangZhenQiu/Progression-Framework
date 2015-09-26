package edu.uci.eecs.wukong.framework.wkpf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.wkpf.Model.LinkTable;
import edu.uci.eecs.wukong.framework.wkpf.Model.ComponentMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An implementation of in-memory storage for DJA data sent from master
 * for remote programming. It contains an array to store the files, and
 * also a set of operators to read data from it.
 * 
 * File Format:
 * 
 * length lsb
 * length msb
 * file type
 * payload
 * 
 * Link table format
 * 2 bytes: number of links
 * Links:
 *        2 byte little endian src component id
 *        1 byte src port number
 *        2 byte little endian dest component id
 *        1 byte dest port number
 *        
 * Component map format
 * 2 bytes little endian number of components
 * Per component:
 *        2 bytes little endian offset
 * Per component @ component offset:
 *        1 byte little endian number of endpoints
 *        2 bytes wuclass id
 *        Per endpoint
 *            4 byte node address
 *            1 byte port number
 */
public class DJAData {
	private final static Logger LOGGER = LoggerFactory.getLogger(MPTN.class);
	private static final int DEFAULT_SIZE = 1024;
	private AtomicBoolean readable = new AtomicBoolean(false);
	private byte[] buffer;
	private int pos; // The next write position
	private int size; // Total size of the the buffer
	private List<RemoteProgrammingListener> listeners;
	
	public DJAData() {
		this.buffer = new byte[DEFAULT_SIZE];
		this.listeners = new ArrayList<RemoteProgrammingListener>();
		this.pos = 0;
		this.size = DEFAULT_SIZE;
	}
	
	/**
	 * Open the DJAData to write, it will always start from beginning.
	 * Therefore, we need to reset the buffer, at the mean time block
	 * all the extract info operation.
	 */
	public synchronized boolean open() {
		if (readable.compareAndSet(true, false)) {
			this.pos = 0;
			return true;
		} else {
			LOGGER.error("Fail to open DJAData which is already opened");
		}	
		return false;
	}
	
	/**
	 * Close the DJAData, and make it ready for extracting link table 
	 * and component map.
	 * 
	 * TODO(Peter Huang) We need to store the data into a long term storage
	 * for the recovery when reboot the progression server. 
	 * 
	 * @return whether is closed successfully
	 */
	public synchronized boolean commit() {
		if (readable.compareAndSet(false, true)) {
			synchronized(readable) {
				readable.notifyAll();
			}
			// Notify the update of link table and component map
			fireUpdateEvent();
			return true;
		} else {
			LOGGER.error("Fail to close DJAData which is already closed");
		}
		return false;
	}
	
	/**
	 * Append a partition of data into DJAData. The start position is 
	 * the place that writes the data into DJAData.
	 * 
	 * @param start position to write data
	 * @param data byte array
	 * @return whether the operation is successful
	 */
	public synchronized boolean append(int start, byte[] data) {
		if (start < pos) {
			LOGGER.error("Rewrite on a memory place with data");
			return false;
		}
		
		if (start > pos) {
			LOGGER.error("Write to unexpected memory position");
		}
		
		if (start + data.length >= size) {
			expand();
		}
		
		System.arraycopy(data, 0, buffer, start, data.length);
		return true;
	}
	
	/**
	 * Add RemoteProgrammingListener instance.
	 * @param listener
	 */
	public void register(RemoteProgrammingListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Notify all of the listener about the new link table and component map.
	 */
	private void fireUpdateEvent() {
		LinkTable table = extractLinkTable();
		ComponentMap map = extractComponentMap();
		for (RemoteProgrammingListener listener : listeners) {
			listener.update(table, map);
		}
	}
	
	/**
	 * Read a well structured link table from the DJA format 
	 * @return an instance of LinkTable
	 */
	private LinkTable extractLinkTable() {
		access();
		int index = findFileIndex(DJAConstants.DJ_FILETYPE_WKPF_LINK_TABLE);
		if (index == -1) {
			LOGGER.error("Fail to find link table in current DJAData.");
		}
		int length = getPayloadLength(index);
		
		if ((length - 2) % 6 != 0) {
			
		}	
		return new LinkTable();
	}
	
	/**
	 * Read a well structured component map from the DJA format
	 * @return an instance of ComponentMap 
	 */
	private ComponentMap extractComponentMap() {
		access();
		int index = findFileIndex(DJAConstants.DJ_FILETYPE_WKPF_COMPONENT_MAP);
		if (index == -1) {
			LOGGER.error("Fail to find component map in current DJAData");
		}
		int length = getPayloadLength(index);
		
		return new ComponentMap();
	}
	
	/**
	 * Find the start offset of particular file type in DJAData.
	 * 
	 * length lsb
     * length msb
     * file type
     * payload
	 * 
	 * @param type  DJA file type
	 * @return the start position
	 */
	private int findFileIndex(byte type) {
		int start = 0;
		while (start < pos - 3) {
			byte fileType = this.buffer[start + DJAConstants.FILE_TYPE_OFFSET];
			if (type != fileType) {
				start += 3 + getPayloadLength(start);
			}
		}
		
		if (start == pos) {
			start = -1;
		}
		
		return start;
	}
	
	private int getPayloadLength(int start) {
		int lsb = this.buffer[start];
		int msb = this.buffer[start + 1];
		int payloadLength = msb << 8 + lsb;
		return payloadLength;
	}
	
	private void expand() {
		byte[] newBuffer = new byte[size * 2];
		System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
		buffer = newBuffer;
		size = size * 2;
	}
	
	private void access() {
		while(!readable.get()) {
			synchronized(readable) {
				try {
					readable.wait();
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	public static class DJAConstants {
		// File offset constants
		public static final int FILE_LENGTH_OFFSET = 0;
		public static final int FILE_TYPE_OFFSET = 2; // In byte
		public static final int FILE_PAYLOAD_OFFSET = 3;
		public static final int LINK_TABLE_RECORD_SIZE = 6;
		public static final int COMPONENT_END_POINT_RECORD_SIZE = 5;
		// File type value
		public static final byte DJ_FILETYPE_LIB_INFUSION = 0;
		public static final byte DJ_FILETYPE_APP_INFUSION = 1;
		public static final byte DJ_FILETYPE_WKPF_LINK_TABLE = 2;
		public static final byte DJ_FILETYPE_WKPF_COMPONENT_MAP = 3;
		public static final byte DJ_FILETYPE_WKPF_INITVALUES_TABLE = 4;
		public static final byte DJ_FILETYPE_ECOCAST_CAPSULE_BUFFER = 5;
	}
}
