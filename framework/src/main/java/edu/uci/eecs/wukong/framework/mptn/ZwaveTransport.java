package edu.uci.eecs.wukong.framework.mptn;

import edu.uci.eecs.wukong.framework.model.Node;
import java.util.List;

/**
 * Transport interface that will JNI to access ZWAVE driver.
 *
 */
public class ZwaveTransport extends AbstractTransport {

	public ZwaveTransport(String name, String devAddress) {
		super(name, devAddress);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAddressLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void receive() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send_raw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getDeviceType() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void routing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Node> nativeDiscover() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void poll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}

}
