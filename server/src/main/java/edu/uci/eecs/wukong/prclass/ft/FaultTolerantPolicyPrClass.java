package edu.uci.eecs.wukong.prclass.ft;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.model.WKPFMessageType;
import edu.uci.eecs.wukong.framework.prclass.Poller;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;
import edu.uci.eecs.wukong.framework.prclass.SystemPrClass;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;


/**
 * Fault tolerant PrClass is to handle with failure of nodes. The PrClass will be responsible for 
 * send update link table messages to related nodes to repair the FBP with backup links. In the end
 * send master a report to tell master the latest FBP setting.
 * 
 * The fault tolerant protocol is like this:
 * 
 * In the component Map, there are information of end-points of a component. The PrClass will be initialized
 * when the fault tolerant policy is selected. It will periodically send the heart-beat message to each 
 * endpoints. All the nodes will reply message with their nodeId. The id information will be delivered to
 * the channel specified by the heartBeat input property.
 * 
 * Fault Tolerant Policy is hidden from user, therefore there is no WuClassId
 * 
 */
@WuClass(id = 10119)
public class FaultTolerantPolicyPrClass extends SystemPrClass {

	@WuProperty(name = "heartBeat", id = 0, type = PropertyType.Input,
			mtype = WKPFMessageType.GetDeviceStatusReturn, dtype = DataType.GlobalChannel)
	private byte heartBeat;

	@WuProperty(name = "changeComponent", id = 1, type = PropertyType.Input,
			mtype = WKPFMessageType.ChangeComponentMapReturn, dtype = DataType.GlobalChannel)

	private byte changeComponent;

	public FaultTolerantPolicyPrClass(Poller poller, PrClassMetrics metrics) {
		super("FaultTolerantPolicy", poller, metrics);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension> ();
		extensions.add(new FaultTolerantExecutionExtenson(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
