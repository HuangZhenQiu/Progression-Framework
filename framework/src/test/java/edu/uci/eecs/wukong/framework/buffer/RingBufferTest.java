package edu.uci.eecs.wukong.framework.buffer;

import junit.framework.TestCase;
import static org.junit.Assert.*;
public class RingBufferTest extends TestCase{

	public void testWriteRingBuffer() {
		RingBuffer buffer = new RingBuffer(4);
		short a = 0;
		short b = 1;
		short c = 1;
		buffer.put(a);
		buffer.put(b);
		buffer.put(c);
		byte[] buf = new byte[4];
		byte[] result = new byte[4];
		result[0] = 1;
		result[1] = 0;
		result[2] = 1;
		result[3] = 0;
		buffer.get(buf, 0, 4);
		assertArrayEquals(buf, result);
	}
}
