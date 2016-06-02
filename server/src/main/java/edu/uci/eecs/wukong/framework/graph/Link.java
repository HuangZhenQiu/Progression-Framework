package edu.uci.eecs.wukong.framework.graph;

import edu.uci.eecs.wukong.framework.entity.Entity;
import java.lang.Class;

/**
 * The link class represents the connection between two nodes in a DAG. It contains
 * the information about the super class of a message object that can be transfered 
 * between them. 
 *
 * @param <T> extends Entity
 */

public class Link<T extends Entity> {
	private final Class<T> type;
	private Node source;
	private Node sink;
	
	public Link(Node source, Node sink, Class<T> type) {
		this.source = source;
		this.sink = sink;
		this.type = type;
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getSink() {
		return sink;
	}

	public void setSink(Node sink) {
		this.sink = sink;
	}
	
	public boolean check(Entity entity) {
		return type.isInstance(entity);
	}
}
