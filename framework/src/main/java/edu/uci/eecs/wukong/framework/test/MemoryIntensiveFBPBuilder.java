package edu.uci.eecs.wukong.framework.test;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.test.LoadGenerator.Activity;

/**
 * This FBP builder aims to test the response of single activity analysis Prclass.
 * In the mock fbp, there are three components lays in a line.
 * 
 * Mock data generator ---->  ActivityRecgonitionPrClass -----> Performance Collector
 *         PC                         Raspberry Pi                     PC
 * 
 * 
 * @author peter
 *
 */
public class MemoryIntensiveFBPBuilder extends MockFBPBuilder {

	// The meta info of the localization PrClass
	private static final short ACTIVITY_WUCLASS_ID = 10115;
	private static final byte ACTIVITY_PRCLASS_PORT = 16;
	
	// The fake wuobject that will generate signal to localization prclass
	private static final short MOCK_MOBILE_INPUT_WUCLASS_ID = 20114;
	private static final byte MOCK_MOBILE_INPUT_PORT = 1;
	
	// The fake wuobject that will receive result form localication prclass;
	private static final short MOCK_MOBILE_OUTPUT_WUCLASS_ID = 20114;
	private static final byte MOCK_MOBILE_OUTPUT_PORT = 2;
	
	public MemoryIntensiveFBPBuilder(MockReprogrammer reprogrammer) {
		super(reprogrammer);
	}

	@Override
	protected void createComponentMap() {
		// Node 1: signal generator
		this.reprogrammer.addPrObject(MOCK_MOBILE_INPUT_WUCLASS_ID, (int)MockGateway.MOCK_GATEWAY_ADDRESS, MOCK_MOBILE_INPUT_PORT);
		// Node 2: localization prclass
		this.reprogrammer.addPrObject(ACTIVITY_WUCLASS_ID, MockGateway.longAddress, ACTIVITY_PRCLASS_PORT);
		// Node 3: location feedback receiver
		this.reprogrammer.addPrObject(MOCK_MOBILE_OUTPUT_WUCLASS_ID, (int)MockGateway.MOCK_GATEWAY_ADDRESS, MOCK_MOBILE_OUTPUT_PORT);
	}

	@Override
	protected void createInitValueTable() {
	}

	@Override
	protected void createLinkTable() {
		// Links from signal generator to prclass
		this.reprogrammer.addLink((short)1, (byte)1, (short)2, (byte)0);
		// link from prclass to receiver
		this.reprogrammer.addLink((short)2, (byte)8, (short)3, (byte)1);
	}

	@Override
	protected List<LoadGenerator<?>> createLoadGenerator() {
		List<LoadGenerator<?>> loadGenerators = new ArrayList<LoadGenerator<?>> ();
		LoadGenerator<Activity> locationGenerator = new LoadGenerator.RandomActivityGenerator(
				ACTIVITY_WUCLASS_ID, ACTIVITY_PRCLASS_PORT, (byte)0, false);
		loadGenerators.add(locationGenerator);
		
		return loadGenerators;
	}
}
