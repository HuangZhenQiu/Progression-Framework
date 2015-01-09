package edu.uci.eecs.wukong.framework.entity;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationCommand {
	private static ConfigurationCommand emptyCommand;
	private List<ConfigurationEntity> entities;
	private boolean isDelayed;
	private int seconds;
	
	public static ConfigurationCommand getEmptyCommand() {
		if (emptyCommand == null) {
			emptyCommand = new ConfigurationCommand(new ArrayList<ConfigurationEntity>());
		}
		
		return emptyCommand;
	}
	
	public ConfigurationCommand(List<ConfigurationEntity> entities) {
		this.entities = entities;
		this.isDelayed = false;
		this.seconds = 0;
	}

	public ConfigurationCommand(List<ConfigurationEntity> entities, int seconds) {
		this.entities = entities;
		this.isDelayed = true;
		this.seconds = seconds;
	}

	public List<ConfigurationEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<ConfigurationEntity> entities) {
		this.entities = entities;
	}

	public boolean isDelayed() {
		return isDelayed;
	}

	public void setDelayed(boolean isDelayed) {
		this.isDelayed = isDelayed;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
}
