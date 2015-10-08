package edu.uci.eecs.wukong.framework.client;

import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.client.FactorClientListener;

/**
 * Interface for integrating with pub/sub system for receiving factor updates
 */
public interface FactorClient {

	public void publish(String topic, BaseFactor factor);
	
	public void subscribe(String topic,  FactorClientListener listener);
}
