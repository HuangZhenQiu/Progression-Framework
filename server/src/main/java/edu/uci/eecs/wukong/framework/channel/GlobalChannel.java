package edu.uci.eecs.wukong.framework.channel;

import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.model.NPP;
import edu.uci.eecs.wukong.framework.model.WKPFMessageType;

/**
 * GlobalChannel is to receive real-time message of particular WKPF type
 * such as, WKPF_GET_COUNTER_RETURN and WKPF_GET_DEVICE_STATUS_RETURN.
 * 
 * @author peter
 * 
 */
public class GlobalChannel<T> extends Channel<T>{
	private WKPFMessageType type;
	
	public GlobalChannel(WKPFMessageType messageType) {
		this.type = messageType;
	}

	public void append(NPP npp, WKPFMessageType type, T data) {
		ChannelData<T> channelData = new ChannelData<T>(npp, data);
		for (Channelable<T> listener : listeners) {
			listener.execute(channelData);
		}
	}
}
