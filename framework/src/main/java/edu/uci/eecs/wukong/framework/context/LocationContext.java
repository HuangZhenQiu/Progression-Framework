package edu.uci.eecs.wukong.framework.context;


public class LocationContext extends Context {
	private String place;
	private String population;
	
	public LocationContext(String topicId) {
		super(topicId);
	}
	
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getPopulation() {
		return population;
	}
	public void setPopulation(String population) {
		this.population = population;
	}
	
	public String toXML() {
		return "";
	}
	
	public String getElementName() {
		return "";
	}
}
