package edu.uci.eecs.wukong.framework.mptn;

import java.nio.ByteBuffer;

public class MPTNPackageParser<T extends AbstractMPTNPackage> {
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
