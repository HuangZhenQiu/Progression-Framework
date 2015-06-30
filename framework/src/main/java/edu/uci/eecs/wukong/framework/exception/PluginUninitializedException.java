package edu.uci.eecs.wukong.framework.exception;

public class PluginUninitializedException extends Exception {
	private static final long serialVersionUID = 123456L;

	public PluginUninitializedException(String message) {
		super(message);
	}
}
