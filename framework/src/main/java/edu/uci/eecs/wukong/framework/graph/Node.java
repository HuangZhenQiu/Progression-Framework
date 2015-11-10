package edu.uci.eecs.wukong.framework.graph;

import java.util.List;
import java.util.ArrayList;

/**
 * The data processing flow is represented as a Directed Acyclic Graph (DAG). On a DAG, 
 * a node represents a process stage. It contains an specific extension point that really
 * handle with the callback logic in the extension of a PrClass
 */
public class Node<T extends ExtensionPoint<?>> {
	private T extensionPoint;
	private List<Node<?>> subsequentNodes;
	
	public Node(T point) {
		extensionPoint = point;
		subsequentNodes = new ArrayList<Node<?>> ();
	}
	
	protected void append(Node<?> node) {
		subsequentNodes.add(node);
	}
}
