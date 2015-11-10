package edu.uci.eecs.wukong.framework.policy.energy;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.SystemPrClass;


/**
 * A system management PrClass is added, when deploy a FBP with an energy efficient policy. The logic of the class
 * to track the transmission data size of each link of a FBP. If find an imbalance situation of it, then notify master 
 * to re-map the application.
 */
public class EnergyPolicyPrClass extends SystemPrClass {

	public EnergyPolicyPrClass(String name) {
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
