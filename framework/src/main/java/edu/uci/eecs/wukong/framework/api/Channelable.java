package edu.uci.eecs.wukong.framework.api;

import edu.uci.eecs.wukong.framework.model.ChannelData;

/**
 * This interface used for plugin which has channel type input property.
 * Progression Framework will trigger the execute function, if a progresison extension
 * implements this interface.
 * 
 */
public interface Channelable {

	public void execute(ChannelData data);
}
