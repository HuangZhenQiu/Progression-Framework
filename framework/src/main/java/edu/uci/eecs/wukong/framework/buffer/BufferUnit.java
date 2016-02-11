package edu.uci.eecs.wukong.framework.buffer;

import java.nio.ByteBuffer;

public interface BufferUnit<T> {

	public void parse(ByteBuffer buffer);
	
	public T getValue();
	
	public int size();
	
	public byte[] toArray();
}
