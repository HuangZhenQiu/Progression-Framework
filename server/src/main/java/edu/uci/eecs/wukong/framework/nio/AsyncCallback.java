package edu.uci.eecs.wukong.framework.nio;

public class AsyncCallback<T> {
	private int destId;
	private long timeout; // Deadline for timeout
	private T value;
	
	public AsyncCallback(int destId,  T value) {
		this.destId = destId;
		this.value = value;
	}
	
	public boolean ready() {
		return value != null;
	}
	
	
	public T get() {
		return value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public boolean timeout() {
		return System.currentTimeMillis() > timeout;
	}
}