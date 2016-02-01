package edu.uci.eecs.wukong.framework.test;

import java.util.List;

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
