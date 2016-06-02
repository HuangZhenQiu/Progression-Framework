package edu.uci.eecs.wukong.framework.factor;


public class LocationFactor extends BaseFactor {
	private String place;
	private String population;
	
	public LocationFactor(String topicId) {
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
