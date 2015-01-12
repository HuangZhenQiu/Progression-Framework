package edu.uci.eecs.wukong.framework.entity;

import edu.uci.eecs.wukong.framework.manager.ConfigurationManager.ConfigurationType;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationCommand {
	private static List<ConfigurationCommand> emptyCommands;
	private String target;
	private ConfigurationType type;
	private List<Entity> entities;
	private boolean isDelayed;
	private int seconds;
	
	public static List<ConfigurationCommand> getEmptyCommand() {
		if (emptyCommands == null) {
			emptyCommands = new ArrayList<ConfigurationCommand>();
		}
		
		return emptyCommands;
	}
	
	public ConfigurationCommand(String target, ConfigurationType type) {
		this.target = target;
		this.type = type;
		this.isDelayed = false;
		this.seconds = 0;
		this.entities =  new ArrayList<Entity>();
	}

	public ConfigurationCommand(String target, ConfigurationType type, int seconds) {
		this.isDelayed = true;
		this.target = target;
		this.type = type;
		this.seconds = seconds;
		this.entities =  new ArrayList<Entity>();
	}
	
	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}
	
	public ConfigurationType getType() {
		return type;
	}
	
	public String getTarget() {
		return target;
	}

	public List<Entity> getEntities() {
		return entities;
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
