package edu.uci.eecs.wukong.framework.mptn;

import java.nio.ByteBuffer;

public abstract class AbstractMPTNPackage {
	public abstract AbstractMPTNPackage parse(ByteBuffer buffer);
}
