package edu.uci.eecs.wukong.framework.transport;

public abstract class Transport implements RPCHandler {
	protected String name;
	protected String address;
	protected Mode mode;

	public static enum Mode {
		STOP,
		ADD,
		DEL
	}
	
	protected Transport(String name, String address) {
		this.name = name;
		this.address = address;
		this.mode = Mode.STOP;
	}
	
	/*public  setUpdateAddressDB() {
		
	}*/
	
	public String getName() {
		return this.name;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public abstract int getAddressLength();
	
	public abstract Mode getLearningMode();
	
	public abstract Object[] receive(int waitmsecond);
	
	public abstract boolean sendRaw(String address, byte[] pyalod);
	
	protected abstract String[] nativeDiscover();
	
	@Override
	public String[] discover() {
		return nativeDiscover();
	}
}
