package edu.uci.eecs.wukong.framework.wkpf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.wkpf.Model.LinkTable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An implementation of in-memory storage for DJA data sent from master
 * for remote programming. It contains an array to store the files, and
 * also a set of operator to read data from it.
 *
 */
public class DJAData {
	private final static Logger LOGGER = LoggerFactory.getLogger(MPTN.class);
	private static final int DEFAULT_SIZE = 1000;
	private AtomicBoolean readable = new AtomicBoolean(true);
	private byte[] buffer;
	private int pos; // The next write position
	private int size; // Total size of the the buffer
	
	public DJAData() {
		this.buffer = new byte[DEFAULT_SIZE];
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
	
	
	public synchronized boolean close() {
		if (readable.compareAndSet(false, true)) {
			synchronized(readable) {
				readable.notifyAll();
			}
			
			return true;
		} else {
			LOGGER.error("Fail to close DJAData which is already closed");
		}
		
		return false;
	}
	
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
	
	private void expand() {
		byte[] newBuffer = new byte[size * 2];
		System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
		buffer = newBuffer;
		size = size * 2;
		
	}
	
	public LinkTable extractLinkTable() {
		access();
		
		return new LinkTable();
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
	
	private int findFileIndex(byte type) {
		int start = 0;
		return start;
	}
	
	public static class DJAConstants {
		// File offset constants
		public static final int FILE_LENGTH_OFFSET = 0;
		public static final int FILE_TYPE_OFFSET = 2; // In byte
		public static final int FILE_PAYLOAD_OFFSET = 3;
		
		// File type value
		public static final byte DJ_FILETYPE_LIB_INFUSION = 0;
		public static final byte DJ_FILETYPE_APP_INFUSION = 1;
		public static final byte DJ_FILETYPE_WKPF_LINK_TABLE = 2;
		public static final byte DJ_FILETYPE_WKPF_COMPONENT_MAP = 3;
		public static final byte DJ_FILETYPE_WKPF_INITVALUES_TABLE = 4;
		public static final byte DJ_FILETYPE_ECOCAST_CAPSULE_BUFFER = 5;
	}
}
