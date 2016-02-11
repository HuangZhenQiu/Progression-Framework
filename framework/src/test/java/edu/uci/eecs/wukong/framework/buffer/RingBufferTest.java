package edu.uci.eecs.wukong.framework.buffer;

import junit.framework.TestCase;
import static org.junit.Assert.*;

import org.junit.Test;
public class RingBufferTest extends TestCase{

	@Test
	public void testWriteRingBuffer() {
		RingBuffer buffer = new RingBuffer(4);
		short a = 0;
		short b = 1;
		short c = 1;
		buffer.appendShort(a);
		buffer.appendShort(b);
		buffer.appendShort(c);
		byte[] buf = new byte[4];
		byte[] result = new byte[4];
		result[0] = 0;
		result[1] = 1;
		result[2] = 0;
		result[3] = 1;
		buffer.get(buf, 0, 4);
		assertArrayEquals(buf, result);
	}
}
