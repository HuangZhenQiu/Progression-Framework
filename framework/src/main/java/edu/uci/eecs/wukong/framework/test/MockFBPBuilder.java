package edu.uci.eecs.wukong.framework.test;

/**
 * MockFBPBuilder is an abstract FBP builder for building different test scenarios
 * 
 * @author peterhuang
 * 
 */
public abstract class MockFBPBuilder {
	public static final int MOCK_GATEWAY_ID = 1;
	public static final int PROGRESSION_SERVER_ID = 2;
	
	protected MockReprogrammer reprogrammer;
	public MockFBPBuilder(MockReprogrammer reprogrammer) {
		this.reprogrammer = reprogrammer;
	}
	
	public byte[] build() {
		createComponentMap();
		createInitValueTable();
		createLinkTable();
		
		return reprogrammer.toByteArray();
	}
	
	protected abstract void createComponentMap();
	
	
	protected abstract void createInitValueTable();
	
	
	protected abstract void createLinkTable();
}
