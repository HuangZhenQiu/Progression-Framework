package edu.uci.eecs.wukong.framework.mptn;

import edu.uci.eecs.wukong.framework.model.Node;
import java.util.List;

public interface RPCHandler {

	public void send();
	
	public void getDeviceType();
	
	public void routing();
	
	public List<Node> discover();
	
	public void add();
	
	public void delete();
	
	public void stop();
	
	public void poll();
}
