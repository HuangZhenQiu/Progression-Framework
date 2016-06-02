package edu.uci.eecs.wukong.framework.entity;

import java.util.List;
import java.util.ArrayList;

public class ConfigurationReport {
	private String applicationId;
	private List<Entity> entities;
	
	public ConfigurationReport(String appId, Entity entity) {
		this.applicationId = appId;
		this.entities = new ArrayList<Entity>();
		this.entities.add(entity);
		
	}
	public ConfigurationReport(String appId, List<Entity> entities) {
		this.applicationId = appId;
		this.entities = entities;
	}
	
	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
	
}
