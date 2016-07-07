package edu.uci.eecs.wukong.framework.manager;

import edu.uci.eecs.wukong.framework.buffer.BufferManager;
import edu.uci.eecs.wukong.framework.factor.SceneManager;
import edu.uci.eecs.wukong.framework.pipeline.BasicPipeline;
import edu.uci.eecs.wukong.framework.prclass.PluginManager;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;

import org.junit.Test;
import org.junit.Before;

import junit.framework.TestCase;

public class PluginManagerTest extends TestCase {
	private PluginManager manager;
	
	@Before
	public void setup() {
		BufferManager bufferManager = new BufferManager();
		manager = new PluginManager(new WKPF(bufferManager, false), new SceneManager(), new BasicPipeline(), bufferManager, null, false);
	}
	
	@Test
	public void test() {
		
	}
}
