package edu.uci.eecs.wukong.framework.model;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.model.Link;

public class LinkTable {
	private List<Link> links;
	public LinkTable() {
		links = new ArrayList<Link> ();
	}
	
	public void addLink(Link link) {
		this.links.add(link);
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
