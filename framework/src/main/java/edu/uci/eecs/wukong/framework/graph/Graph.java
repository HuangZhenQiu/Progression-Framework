package edu.uci.eecs.wukong.framework.graph;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

public abstract class Graph {
	private Set<Node<?>> nodes;
	private Map<ExtensionPoint, Node<?>> nodeMap;

	public Graph() {
		nodes = new HashSet<Node<?>> ();
		nodeMap = new HashMap<ExtensionPoint, Node<?>> (); 
	}
	
	public void addNode(Node<?> node) {
		nodes.add(node);
		nodeMap.put(node.getExtensionPoint(), node);
	}
	
	public void addLink(Node<?> start, Node<?> end) {
		start.append(end);
	}
}
