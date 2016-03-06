package edu.uci.eecs.wukong.framework.policy.ft;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;
import edu.uci.eecs.wukong.framework.prclass.SystemPrClass;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;


/**
 * Fault tolerant PrClass is to handle with failure of nodes. The Prclass will be responsible for 
 * send update link table messages to related nodes to repair the FBP with backup links. In the end
 * send master a report to tell master the latest FBP setting.
 * 
 * The fault tolerant protocol is like this:
 * 
 * In the component Map, there are information of end-points of a component. The PrClass will be initialized
 * when the fault tolerant policy is selected. It will periodically send the heart-beat message to each 
 * endpoints. All the nodes will reply message with their nodeId. The id information will be deliveried to
 * the channel specified by the heartBeat input property.
 * 
 * Fault Tolerant Policy is hidden from user, therefore there is no WuClassId
 * 
 */
public class FaultTolerantPolicyPrClass extends SystemPrClass {

	@WuProperty(name = "heartBeat", id = 0, type = PropertyType.Input, dtype = DataType.Channel)
	private short heartBeat;

	public FaultTolerantPolicyPrClass(WKPF wkpf, PrClassMetrics metrics) {
		super("FaultTolerantPolicy", wkpf, metrics);
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
