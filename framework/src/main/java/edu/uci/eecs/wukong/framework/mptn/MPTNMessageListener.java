package edu.uci.eecs.wukong.framework.mptn;

import java.nio.ByteBuffer;

public interface MPTNMessageListener {
	public void onMessage(ByteBuffer bytes);
}
