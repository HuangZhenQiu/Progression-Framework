package edu.uci.eecs.wukong.framework.graph;

import edu.uci.eecs.wukong.framework.entity.Entity;

/**
 * The data processing flow is represented as a Directed Acyclic Graph (DAG). On a DAG, 
 * a node represents a process stage. It contains an specific extension point that really
 * handle with the callback logic in the extension of a PrClass
 */
public abstract class Node implements Runnable {
	private Graph graph;
	
	public Node(Graph graph) {
		this.graph = graph;
	}
	
	public void send(Entity entity) {
		graph.send(this, entity);
	}
	
	public abstract void append(Entity entity);
}
