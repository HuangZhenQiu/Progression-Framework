package edu.uci.eecs.wukong.framework.entity;

import java.util.List;
import java.util.ArrayList;

public class ConfigurationReport {
	private String applicationId;
	private List<ConfigurationEntity> entities;
	
	public ConfigurationReport(String appId, ConfigurationEntity entity) {
		this.applicationId = appId;
		this.entities = new ArrayList<ConfigurationEntity>();
		this.entities.add(entity);
		
	}
	public ConfigurationReport(String appId, List<ConfigurationEntity> entities) {
		this.applicationId = appId;
		this.entities = entities;
	}
	
	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public List<ConfigurationEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<ConfigurationEntity> entities) {
		this.entities = entities;
	}
	
	
}
