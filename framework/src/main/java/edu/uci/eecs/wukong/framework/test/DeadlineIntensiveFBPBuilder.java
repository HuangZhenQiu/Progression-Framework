package edu.uci.eecs.wukong.framework.test;

/**
 * This FBP builder aims to test the throughput of single localization prclass.
 * In the mock fbp, there are three components lays in a line. 
 * 
 * @author peter
 *
 */
public class DeadlineIntensiveFBPBuilder extends MockFBPBuilder {
	// The meta info of the localization PrClass
	private static final short LOCALIZATION_WUCLASS_ID = 10115;
	private static final int PROGRESSION_SERVER_LONG_ADDRESS = 2;
	private static final byte LOCALIZATION_PRCLASS_PORT = 16;
	
	// The fake wuobject that will generate signal to localization prclass
	private static final short MOCK_MOBILE_INPUT_WUCLASS_ID = 20114;
	private static final byte MOCK_MOBILE_INPUT_PORT = 1;
	
	// The fake wuobject that will receive result form localication prclass;
	private static final short MOCK_MOBILE_OUTPUT_WUCLASS_ID = 20114;
	private static final byte MOCK_MOBILE_OUTPUT_PORT = 2;
	
	public DeadlineIntensiveFBPBuilder(MockReprogrammer reprogrammer) {
		super(reprogrammer);
	}

	@Override
	protected void createComponentMap() {
		// Node 1: signal generator
		this.reprogrammer.addPrObject(MOCK_MOBILE_INPUT_WUCLASS_ID, (int)MockGateway.MOCK_GATEWAY_ADDRESS, MOCK_MOBILE_INPUT_PORT);
		// Node 2: localization prclass
		this.reprogrammer.addPrObject(LOCALIZATION_WUCLASS_ID, PROGRESSION_SERVER_LONG_ADDRESS, LOCALIZATION_PRCLASS_PORT);
		// Node 3: location feedback receiver
		this.reprogrammer.addPrObject(MOCK_MOBILE_OUTPUT_WUCLASS_ID, (int)MockGateway.MOCK_GATEWAY_ADDRESS, MOCK_MOBILE_OUTPUT_PORT);
	}

	@Override
	protected void createInitValueTable() {
		byte[] particleCount = {0, 100};
		this.reprogrammer.addInitValue((short)2, (byte)4, particleCount);
		byte[] movNoise = {0};
		this.reprogrammer.addInitValue((short)2, (byte)5, movNoise);
		byte[] rotNoise = {0};
		this.reprogrammer.addInitValue((short)2, (byte)6, rotNoise);
		byte[] senseNoise = {0};
		this.reprogrammer.addInitValue((short)2, (byte)7, senseNoise);
		byte[] maxr = {0};
		this.reprogrammer.addInitValue((short)2, (byte)7, maxr);
	}

	@Override
	protected void createLinkTable() {
		// Links from signal generator to prclass
		this.reprogrammer.addLink((short)1, (byte)1, (short)2, (byte)1); //signalX
		this.reprogrammer.addLink((short)1, (byte)1, (short)2, (byte)2); //signalY
		this.reprogrammer.addLink((short)1, (byte)1, (short)2, (byte)3); //singalZ
		
		// link from prclass to receiver
		this.reprogrammer.addLink((short)2, (byte)8, (short)3, (byte)1);
	}

}
