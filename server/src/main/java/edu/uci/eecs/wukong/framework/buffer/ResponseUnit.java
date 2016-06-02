package edu.uci.eecs.wukong.framework.buffer;

import java.nio.ByteBuffer;

import edu.uci.eecs.wukong.framework.property.Response;

public class ResponseUnit implements BufferUnit<Response> {
	private Response value;
	
	public ResponseUnit() {};
	public ResponseUnit(Response value) {
		this.value = value;
	}
	
	@Override
	public void parse(ByteBuffer buffer, boolean withSequence) {
		value = new Response(buffer.getInt());	
	}

	@Override
	public Response getValue() {
		return value;
	}

	@Override
	public int size() {
		return 4;
	}

	@Override
	public byte[] toArray() {
		ByteBuffer buffer = ByteBuffer.allocate(size());
		buffer.putInt(value.getSequence());
		return buffer.array();
	}

}
