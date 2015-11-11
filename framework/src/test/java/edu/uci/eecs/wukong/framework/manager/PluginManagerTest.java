package edu.uci.eecs.wukong.framework.manager;

import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;

import org.junit.Test;
import org.junit.Before;

import junit.framework.TestCase;

public class PluginManagerTest extends TestCase {
	private PluginManager manager;
	
	@Before
	public void setup() {
		BufferManager bufferManager = new BufferManager();
		manager = new PluginManager(new WKPF(bufferManager), new SceneManager(), new Pipeline(), bufferManager);
	}
	
	@Test
	public void test() {
		
	}
}
