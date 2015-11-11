package edu.uci.eecs.wukong.framework.factor;


/**
 * Interface for integrating with pub/sub system for receiving factor updates
 */
public interface FactorClient {

	public void publish(String topic, BaseFactor factor);
	
	public void subscribe(String topic,  FactorClientListener listener);
}
