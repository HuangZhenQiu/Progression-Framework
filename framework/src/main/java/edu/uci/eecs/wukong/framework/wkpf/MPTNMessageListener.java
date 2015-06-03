package edu.uci.eecs.wukong.framework.wkpf;

import java.nio.ByteBuffer;

public interface MPTNMessageListener {
	public void onMessage(ByteBuffer bytes);
}
