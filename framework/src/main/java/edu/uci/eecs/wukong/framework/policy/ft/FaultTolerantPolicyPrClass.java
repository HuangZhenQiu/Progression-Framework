package edu.uci.eecs.wukong.framework.policy.ft;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.SystemPrClass;


/**
 * Fault tolerant PrClass is to handle with failure of nodes. The Prclass will be responsible for 
 * send update link table messages to related nodes to repair the FBP with backup links. In the end
 * send master a report to tell master the latest FBP setting.
 */
public class FaultTolerantPolicyPrClass extends SystemPrClass {

	public FaultTolerantPolicyPrClass(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Extension> registerExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> registerContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
