package edu.uci.eecs.wukong.framework.wkpf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.model.Component;
import edu.uci.eecs.wukong.framework.model.ComponentMap;
import edu.uci.eecs.wukong.framework.model.EndPoint;
import edu.uci.eecs.wukong.framework.model.Link;
import edu.uci.eecs.wukong.framework.model.LinkTable;

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
		
		LinkTable table = new LinkTable();
		
		// get the size of links
		int size = getLittleEndianShort(index);
		// start index of the links
		int start = index + 2;
		for (int i = 0; i < size; i++) {
			Link link = extractLink(start + i * DJAConstants.LINK_TABLE_RECORD_SIZE);
			table.addLink(link);
		}
		
		return table;
	}
	
	/**
	 * Extract a link from the buffer from the start index
	 * 
	 * Link Format:
	 * 2 byte little endian src component id
	 * 1 byte src port number
	 * 2 byte little endian dest component id
	 * 1 byte dest port number
	 */
	private Link extractLink(int start) {
		int srcComponentId = this.getLittleEndianShort(start);
		int destComponentId = this.getLittleEndianShort(start + 3);
		
		return new Link(srcComponentId, buffer[start + 2], destComponentId, buffer[start + 5]);
	}
	
	/**
	 * Extract a well structured component map from the DJA buffer
	 * @return an instance of ComponentMap 
	 * 
	 * Component map format
	 * 2 bytes little endian number of components
	 * Per component:
	 *    2 bytes little endian offset
	 */
	private ComponentMap extractComponentMap() {
		access();
		int index = findFileIndex(DJAConstants.DJ_FILETYPE_WKPF_COMPONENT_MAP);
		if (index == -1) {
			LOGGER.error("Fail to find component map in current DJAData");
		}
		
		ComponentMap componentMap = new ComponentMap();
		// Size of component
		int size = getLittleEndianShort(index);
		// start of component offset table;
		int start = index + 2;
		for (int i = 0; i < size; i++) {
			// the offset relative to the start of component map
			int componentOffset = getLittleEndianShort(start + i * 2);
			componentMap.addComponent(extractComponent(index + componentOffset));
		}
		
		return componentMap;
	}
	
	/**
	 * Extract a component from the DJA buffer.
	 * @return a instance of Component
	 * 
	 * Per component @ component offset:
	 *   1 byte little endian number of endpoints
	 *   2 bytes wuclass id
	 *   EndPoint Table
	 */
	private Component extractComponent(int index) {
		int endpointSize = buffer[index];
		short wuclassId = (short) getLittleEndianShort(index + 1);
	    
		Component component = new Component(wuclassId);
		// Start of end point table
		int start = index + 3;
		for (int i = 0; i < endpointSize; i++) {
			component.addEndPoint(extractEndPoint(start + i * DJAConstants.COMPONENT_END_POINT_RECORD_SIZE));
		}
		
		return component;
	}
	
	/**
	 * Extract an Endpoint from the buffer at position index
	 * @param index of buffer
	 * @return an instance of EndPoint
	 * 
	 * Per endpoint
	 *     4 byte node address
	 *     1 byte port number
	 */
	private EndPoint extractEndPoint(int index) {
		int nodeId = getLittleEndianInteger(index);
		return new EndPoint(nodeId, buffer[index + 4]);
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
				start += 3 + getLittleEndianShort(start);
			}
		}
		
		if (start == pos) {
			start = -1;
		}
		
		return start;
	}
	
	/**
	 * Get little endian short from the start index of the buffer
	 * @param start the index in the buffer
	 * @return the converted short
	 */
	private short getLittleEndianShort(int start) {
		int result = this.buffer[start];
		int msb = this.buffer[start + 1];
		return (short) (result + msb << 8);
	}
	
	/**
	 * Get little endian int from the start index of the buffer
	 * @param start the index of the buffer
	 * @return the converted int
	 */
	private int getLittleEndianInteger(int start) {
		int result = buffer[start];
		result += buffer[start + 1] << 8;
		result += buffer[start + 2] << 16;
		result += buffer[start + 3] << 24;
		
		return result;
	}
	
	/**
	 * Expand the size of buffer, when the data to write exceed the size of buffer
	 */
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
	
	public boolean isReadable() {
		return this.readable.get();
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
