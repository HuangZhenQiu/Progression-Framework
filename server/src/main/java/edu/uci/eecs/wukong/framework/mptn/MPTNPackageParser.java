package edu.uci.eecs.wukong.framework.mptn;

import java.nio.ByteBuffer;

import edu.uci.eecs.wukong.framework.mptn.packet.AbstractMPTNPacket;

public class MPTNPackageParser<T extends AbstractMPTNPacket> {
	private Class<T> type;
	
	public MPTNPackageParser(Class<T> type) {
		this.type = type;
	}
	public T parse(ByteBuffer buffer){
		try {
			T message = type.newInstance();
			message.parse(buffer);
			return message;
		} catch (Exception e) {
			
		}
		
		return null;
	}
}
