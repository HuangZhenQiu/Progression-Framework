package edu.uci.eecs.wukong.framework.test;

import java.util.List;

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

	public MemoryIntensiveFBPBuilder(MockReprogrammer reprogrammer) {
		super(reprogrammer);
	}
	
	@Override
	protected void createComponentMap() {
		
	}

	@Override
	protected void createInitValueTable() {
		
	}

	@Override
	protected void createLinkTable() {
		
	}

	@Override
	protected List<LoadGenerator<?>> createLoadGenerator() {
		// TODO Auto-generated method stub
		return null;
	}
}
