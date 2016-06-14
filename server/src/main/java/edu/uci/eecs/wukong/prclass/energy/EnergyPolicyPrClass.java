package edu.uci.eecs.wukong.prclass.energy;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;
import edu.uci.eecs.wukong.framework.prclass.SystemPrClass;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;


/**
 * A system management PrClass is added, when deploy a FBP with an energy efficient policy. The logic of the class
 * to track the transmission data size of each link of a FBP. If find an imbalance situation of it, then notify master 
 * to re-map the application.
 */

@WuClass(id = 10117)
public class EnergyPolicyPrClass extends SystemPrClass {
	@WuProperty(id = 0, name="input", type = PropertyType.Input, dtype = DataType.Channel)
	private short input;

	public EnergyPolicyPrClass(WKPF wkpf, PrClassMetrics metrics) {
		super("EnergyPolicy", wkpf, metrics);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension> ();
		extensions.add(new EnergyPolicyProgressionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		return null;
	}
}
