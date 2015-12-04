package edu.uci.eecs.wukong.framework.wkpf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.model.Component;
import edu.uci.eecs.wukong.framework.model.ComponentMap;
import edu.uci.eecs.wukong.framework.model.EndPoint;
import edu.uci.eecs.wukong.framework.model.Link;
import edu.uci.eecs.wukong.framework.model.LinkTable;
import edu.uci.eecs.wukong.framework.model.InitValue;
import edu.uci.eecs.wukong.framework.model.InitValueTable;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;

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
	private final static Logger LOGGER = LoggerFactory.getLogger(DJAData.class);
	public static final int DEFAULT_PAGE_SIZE = 1024;
	private AtomicBoolean readable = new AtomicBoolean(false);
	private byte[] buffer;
	private int pos; // The next write position
	private int size; // Total size of the the buffer
	private List<RemoteProgrammingListener> listeners;
	
	public DJAData() {
		this.buffer = new byte[DEFAULT_PAGE_SIZE * 10];
		this.listeners = new ArrayList<RemoteProgrammingListener>();
		this.pos = 0;
		this.size = DEFAULT_PAGE_SIZE *  10;
	}
	
	/**
	 * Open the DJAData to write, it will always start from beginning.
	 * Therefore, we need to reset the buffer, at the mean time block
	 * all the extract info operation.
	 */
	public synchronized boolean open(int totalSize) {
		this.readable.set(false);
		if (totalSize > size) {
			expand();
		}
		this.pos = 0;
		return true;
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
		synchronized(readable) {
			readable.set(true);;
			readable.notifyAll();
		}
		return true;
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
		LOGGER.info("Start to append data into DJAData at position " + start + " with data length " + data.length);
		if (start < pos) {
			LOGGER.error("Rewrite on a memory place with data");
			return false;
		}
		
		if (start > pos) {
			LOGGER.error("Write to unexpected memory position");
			return false;
		}
		
		if (start + data.length >= size - 1) {
			expand();
		}
		
		System.arraycopy(data, 0, buffer, start, data.length);
		pos += data.length;
		
		LOGGER.error("Appended data into DJAData, next pos is " + pos);
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
	public void fireUpdateEvent() {
		LinkTable table = extractLinkTable();
		ComponentMap map = extractComponentMap();
		InitValueTable initValue = extractInitValueTable();
		for (RemoteProgrammingListener listener : listeners) {
			listener.update(table, map, initValue);
		}
	}
	
	/** Initialisation list format (little endian)
	       2 bytes: number of entries
	       repeat for each entry:
			   2 bytes: component id
			   1 byte: property number
		       1 byte: property value size
		       X bytes: value
	**/
	private InitValueTable extractInitValueTable() {
		access();
		int index = findFileIndex(DJAConstants.DJ_FILETYPE_WKPF_INITVALUES_TABLE);
		InitValueTable table = new InitValueTable();
		if (index == -1) {
			LOGGER.info("There is no init value table  in current DJAData.");
			return table;
		}
		
		// start of init value table
		int start = index + 3;
		// Size of init value table
		int size = WKPFUtil.getLittleEndianShort(buffer, start);
		int offset = 0;
		for (int i = 0; i < size; i++) {
			start += offset;
			InitValue value = extractInitValue(start);
			table.addInitValue(value);
			offset = value.getLength();
		}
		
		LOGGER.info("Extracted init value table information from DJAData: " + table.toString());
		return table;
	}
	
	/**
	 *   2 bytes: component id
	 *   1 byte: property number
	 *	 1 byte: property value size
	 *	 X bytes: value
	 * @param start
	 * @param value
	 * @return
	 */
	private InitValue extractInitValue(int start) {
		short componentId = WKPFUtil.getLittleEndianShort(buffer, start);
		byte propertyNumber = buffer[start + 2];
		byte size = buffer[start + 3];
		byte[] val = new byte[size];
		System.arraycopy(buffer, start + 4, val, 0, size);
		InitValue value = new InitValue(componentId, propertyNumber, size, val);
		
		return value;
	}
	
	/**
	 * Read a well structured link table from the DJA format 
	 * @return an instance of LinkTable
	 */
	private LinkTable extractLinkTable() {
		access();
		int index = findFileIndex(DJAConstants.DJ_FILETYPE_WKPF_LINK_TABLE);
		LinkTable table = new LinkTable();
		if (index == -1) {
			LOGGER.error("Fail to find link table in current DJAData.");
			return table;
		}
		
		// get the size of links
		int size = WKPFUtil.getLittleEndianShort(buffer, index + 3);
		// start index of the links
		int start = index + 5;
		for (int i = 0; i < size; i++) {
			Link link = extractLink(start + i * DJAConstants.LINK_TABLE_RECORD_SIZE);
			table.addLink(link);
		}
		
		LOGGER.info("Extracted link table information from DJAData: " + table.toString());
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
		try {
			short srcComponentId = WKPFUtil.getLittleEndianShort(buffer, start);
			short destComponentId = WKPFUtil.getLittleEndianShort(buffer, start + 3);
			
			Link link = new Link(srcComponentId, buffer[start + 2], destComponentId, buffer[start + 5]);
			return link;
		} catch (Exception e) {
			LOGGER.error("Error Status : fail to extract link from DJAData");
		}
		
		return null;
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
		ComponentMap componentMap = new ComponentMap();
		if (index == -1) {
			LOGGER.error("Fail to find component map in current DJAData");
			return componentMap;
		}
		
		// start of component map
		int start = index + 3;
		// Size of component
		int size = WKPFUtil.getLittleEndianShort(buffer, start);
		// start of component offset table;
		int offsetStart = index + 5;
		LOGGER.info("Start index of component map : " + start);
		for (int i = 0; i < size; i++) {
			// the offset relative to the start of component map
			int componentOffset = WKPFUtil.getLittleEndianShort(buffer, offsetStart + i * 2);
			LOGGER.info("Start index of component " + i + " : " + componentOffset);
			componentMap.addComponent(extractComponent(start + componentOffset));
		}
		
		LOGGER.info("Extracted component map information from DJAData: " + componentMap.toString());
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
		try {
			int endpointSize = buffer[index];
			short wuclassId = (short) WKPFUtil.getLittleEndianShort(buffer, index + 1);
		    
			Component component = new Component(wuclassId);
			// Start of end point table
			int start = index + 3;
			for (int i = 0; i < endpointSize; i++) {
				component.addEndPoint(extractEndPoint(start + i * DJAConstants.COMPONENT_END_POINT_RECORD_SIZE));
			}
			
			return component;
		} catch (Exception e) {
			LOGGER.error("Error Status : fail to extract component from DJAData");
		}		
		return null;
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
		long nodeId = WKPFUtil.getLittleEndianLong(buffer, index);
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
				start += 3 + WKPFUtil.getLittleEndianShort(buffer, start);
			} else {
				return start;
			}
		}
		
		if (start >= pos) {
			start = -1;
		}
		
		return start;
	}
	
	/**
	 * Expand the size of buffer, when the data to write exceed the size of buffer
	 */
	private void expand() {
		LOGGER.info("Expand DJA buffer: Current buffer size is " + buffer.length);
		byte[] newBuffer = new byte[size * 2];
		System.arraycopy(buffer, 0, newBuffer, 0, pos);
		buffer = newBuffer;
		size = size * 2;
		LOGGER.info("After expand DJA buffer: Current buffer size is " + buffer.length);
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
