package edu.uci.eecs.wukong.framework.manager;

import org.junit.Test;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import junit.framework.TestCase;

public class PluginManagerTest extends TestCase {
	
	@Test
	public void testInitilization() throws Exception {
		PluginManager manager = new PluginManager(new ContextManager(), new BufferManager(), new Pipeline());
		manager.init();
	}
	
	@Test
	public void testRegisterPlugin() throws Exception {
		
	}
	
}
