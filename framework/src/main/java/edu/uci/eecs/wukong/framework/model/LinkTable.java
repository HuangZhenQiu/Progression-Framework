package edu.uci.eecs.wukong.framework.model;

import edu.uci.eecs.wukong.framework.model.Link;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class LinkTable {
	public static int LINK_LENGTH = 6;
	private List<Link> links;
	public LinkTable() {
		links = new ArrayList<Link> ();
	}
	
	public void addLink(Link link) {
		this.links.add(link);
	}
	
	public byte[] toByteArray() {
		int length = length();
		ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.put((byte) (length % 256));
		buffer.put((byte) (length / 256));
		for (Link link : links) {
			buffer.put(link.toByteArray());
		}
		
		return buffer.array();
	}
	
	public int length() {
		return 2 + links.size() * LINK_LENGTH;
	}
	
	public List<Link> getOutLinks(int srcId, byte propertyId) {
		List<Link> outLinks = new ArrayList<Link> ();
		for (Link link : links) {
			if (link.getSourceId() == srcId && link.getSourcePid() == propertyId) {
				outLinks.add(link);
			}
		}
		
		return outLinks;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof LinkTable) {
			LinkTable model = (LinkTable) object;
			if (model.links.equals(model.links)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return links.hashCode();
	}
	
	@Override
	public String toString() {
		return links.toString();
	}
	
}
