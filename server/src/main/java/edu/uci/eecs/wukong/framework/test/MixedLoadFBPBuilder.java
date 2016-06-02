package edu.uci.eecs.wukong.framework.test;

import java.util.List;

/**
 * For load test of progression server, we want to evaluate what's the scalability of
 * progression server, what is the response time of localization algorithm
 * 
 * 
 * 
 * @author peterhuang
 *
 */
public class MixedLoadFBPBuilder extends MockFBPBuilder {

	public MixedLoadFBPBuilder(MockReprogrammer reprogrammer) {
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
