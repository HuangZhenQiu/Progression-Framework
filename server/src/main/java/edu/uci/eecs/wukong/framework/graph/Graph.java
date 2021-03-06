package edu.uci.eecs.wukong.framework.graph;

import java.util.List;
import java.util.ArrayList;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

import edu.uci.eecs.wukong.framework.entity.Entity;

/**
 * Abstract class for building up Pipeline. It is responsible for routing entities between extend points.
 *
 */
public class Graph {
	protected Set<Node> nodes;
	protected Map<Node, List<Link<? extends Entity>>> outLinks; 

	public Graph() {
		nodes = new HashSet<Node> ();
		outLinks = new HashMap<Node, List<Link<? extends Entity>>> (); 
	}
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public void addLink(Link<? extends Entity> link) {
		if (outLinks.get(link.getSource()) == null) {
			List<Link<? extends Entity>> links = new ArrayList<Link<? extends Entity>>();
			outLinks.put(link.getSource(),  links);
		}
		
		outLinks.get(link.getSource()).add(link);
		if (!nodes.contains(link.getSource())) {
			addNode(link.getSource());
		}
		if (!nodes.contains(link.getSink())) {
			addNode(link.getSink());
		}
	}
	
	public void send(Node node, Entity entity) {
		List<Link<? extends Entity>> links = outLinks.get(node);
		for (Link<? extends Entity> link : links) {
			if (link.check(entity)) {
				Node sink = link.getSink();
				sink.append(entity);
			}
		}
	}
	
	public int getNodeSize() {
		return nodes.size();
	}
}
