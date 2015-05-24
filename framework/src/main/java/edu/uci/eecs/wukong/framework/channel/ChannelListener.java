package edu.uci.eecs.wukong.framework.channel;

public interface ChannelListener<T extends Number> {
	
	public void onMessage(T data);
}
