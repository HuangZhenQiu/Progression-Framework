package edu.uci.eecs.wukong.framework.graph;

import edu.uci.eecs.wukong.framework.entity.Entity;
import edu.uci.eecs.wukong.framework.graph.ExtensionPoint;
import edu.uci.eecs.wukong.framework.graph.Graph;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Mockito;

public class GraphTest extends TestCase {
	private Pipeline pipeline = Mockito.mock(Pipeline.class);
	private ExtensionPoint<?> nodeA = new MockExtensionPoint(pipeline);
	private ExtensionPoint<?> nodeB = new MockExtensionPoint(pipeline);
	private ExtensionPoint<?> nodeC = new MockExtensionPoint(pipeline);
	
	private static class EntityA extends Entity {

		public EntityA(PipelinePrClass prClass) {
			super(prClass);
		}
	}
	
	private static class EntityB extends Entity {

		public EntityB(PipelinePrClass prClass) {
			super(prClass);
		}
	}
	
	private static class MockExtensionPoint extends ExtensionPoint {

		public MockExtensionPoint(Pipeline pipeline) {
			super(pipeline);
		}

		@Override
		public void run() {}
	}

	
	@Test
	public void testGraphConnectivity() {
		Graph graph = new Graph();
		Link<EntityA> linkAB = new Link<EntityA> (nodeA, nodeB, EntityA.class);
		Link<EntityA> linkAC = new Link<EntityA> (nodeA, nodeC, EntityA.class);
		graph.addLink(linkAB);
		graph.addLink(linkAC);
		
		// Send entityA
		EntityA entityA = Mockito.mock(EntityA.class);
		graph.send(nodeA, entityA);
		assertEquals(1, nodeB.getQueueSize());
		assertEquals(1, nodeC.getQueueSize());
	}
	
	@Test
	public void testGraphLinkCheck() {
		Graph graph = new Graph();
		Link<EntityA> linkAB = new Link<EntityA> (nodeA, nodeB, EntityA.class);
		Link<EntityB> linkAC = new Link<EntityB> (nodeA, nodeC, EntityB.class);
		graph.addLink(linkAB);
		graph.addLink(linkAC);
		
		EntityA entityA = Mockito.mock(EntityA.class);
		graph.send(nodeA, entityA);
		assertEquals(1, nodeB.getQueueSize());
		assertEquals(0, nodeC.getQueueSize());
		
		EntityB entityB = Mockito.mock(EntityB.class);
		graph.send(nodeA, entityB);
		assertEquals(1, nodeB.getQueueSize());
		assertEquals(1, nodeC.getQueueSize());
		
	}
}
