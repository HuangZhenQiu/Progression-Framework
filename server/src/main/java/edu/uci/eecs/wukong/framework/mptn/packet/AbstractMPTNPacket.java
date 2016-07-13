package edu.uci.eecs.wukong.framework.mptn.packet;

import java.nio.ByteBuffer;

public abstract class AbstractMPTNPacket {
	public abstract AbstractMPTNPacket parse(ByteBuffer buffer);
}
