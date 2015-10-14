package edu.uci.eecs.wukong.framework.manager;

import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import org.junit.Test;
import junit.framework.TestCase;

public class PluginManagerTest extends TestCase {
	
	@Test
	public void testInitilization() throws Exception {
		BufferManager bufferManager = new BufferManager();
		PluginManager manager = new PluginManager(new WKPF(bufferManager), new SceneManager(), new Pipeline(), bufferManager);
		manager.init();
	}
	
	@Test
	public void testRegisterPlugin() throws Exception {
		
	}
	
}
