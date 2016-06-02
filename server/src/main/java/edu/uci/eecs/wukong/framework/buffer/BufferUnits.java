package edu.uci.eecs.wukong.framework.buffer;

import java.nio.ByteBuffer;

public class BufferUnits {
	public static class ByteUnit implements BufferUnit<Byte> {
		private byte value;
		
		public ByteUnit() {}
		
		public ByteUnit(byte value) {
			this.value = value;
		}

		@Override
		public Byte getValue() {
			return value;
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public void parse(ByteBuffer buffer, boolean withSequence) {
			this.value = buffer.get();
		}

		@Override
		public byte[] toArray() {
			ByteBuffer buffer = ByteBuffer.allocate(size());
			buffer.put(value);
			return buffer.array();
		}
	}
	
	public static class ShortUnit implements BufferUnit<Short> {
		private short value;
		
		public ShortUnit() {}
		
		public ShortUnit(short value) {
			this.value = value;
		}

		@Override
		public Short getValue() {
			return value;
		}

		@Override
		public int size() {
			return 2;
		}

		@Override
		public void parse(ByteBuffer buffer, boolean withSequence) {
			this.value = buffer.getShort();
		}

		@Override
		public byte[] toArray() {
			ByteBuffer buffer = ByteBuffer.allocate(size());
			buffer.putShort(value);
			return buffer.array();
		}
	}
	
	public static class IntUnit implements BufferUnit<Integer> {
		private int value;
		
		public IntUnit() {}
		
		public IntUnit(int value) {
			this.value = value;
		}

		@Override
		public Integer getValue() {
			return value;
		}

		@Override
		public int size() {
			return 4;
		}
		
		@Override
		public void parse(ByteBuffer buffer, boolean withSequence) {
			this.value = buffer.getInt();
		}

		@Override
		public byte[] toArray() {
			ByteBuffer buffer = ByteBuffer.allocate(size());
			buffer.putInt(value);
			return buffer.array();
		}
	}
	
	public static class LongUnit implements BufferUnit<Long> {
		private long value;
		
		public LongUnit() {}
		
		public LongUnit(long value) {
			this.value = value;
		}

		@Override
		public Long getValue() {
			return value;
		}

		@Override
		public int size() {
			return 8;
		}
		
		@Override
		public void parse(ByteBuffer buffer, boolean withSequence) {
			this.value = buffer.getLong();
		}
		
		@Override
		public byte[] toArray() {
			ByteBuffer buffer = ByteBuffer.allocate(size());
			buffer.putLong(value);
			return buffer.array();
		}
	}
	
	public static class FloatUnit implements BufferUnit<Float> {
		private float value;
		
		public FloatUnit() {}
		
		public FloatUnit(float value) {
			this.value = value;
		}

		@Override
		public void parse(ByteBuffer buffer, boolean withSequence) {
			this.value = buffer.getFloat();
		}

		@Override
		public Float getValue() {
			return value;
		}

		@Override
		public int size() {
			return 4;
		}

		@Override
		public byte[] toArray() {
			ByteBuffer buffer = ByteBuffer.allocate(size());
			buffer.putFloat(value);
			return buffer.array();
		}
	}
}
