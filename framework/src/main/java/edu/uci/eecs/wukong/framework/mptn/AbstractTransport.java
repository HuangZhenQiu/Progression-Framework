package edu.uci.eecs.wukong.framework.mptn;

import edu.uci.eecs.wukong.framework.model.Node;
import edu.uci.eecs.wukong.framework.mptn.MPTN.MODE; 
import java.util.List;

public abstract class AbstractTransport implements LearningHandler, RPCHandler{
	private String name;
	private String devAddress;
	private MODE model;
	
	public AbstractTransport(String name, String devAddress) {
		this.name = name;
		this.devAddress = devAddress;
		this.model = MODE.STOP_MODE;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDevAddress() {
		return this.devAddress;
	}
	
	public abstract int getAddressLength();
	
	public abstract void receive();
	
	public abstract void send_raw();

	public abstract void send();

	public abstract void getDeviceType();

	public abstract void routing();

	public List<Node> discover() {
		List<Node> nodes = nativeDiscover();
		// Store the nodes info into DB
		return nodes;
	}
	
	protected abstract List<Node> nativeDiscover();

	public abstract void poll();

	public abstract void add();

	public abstract void delete();	
}
