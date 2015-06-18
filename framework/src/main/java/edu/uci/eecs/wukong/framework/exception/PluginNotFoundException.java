package edu.uci.eecs.wukong.framework.exception;

public class PluginNotFoundException extends Exception {
	private static final long serialVersionUID = 123456L;

	public PluginNotFoundException(String message) {
		super(message);
	}
}
