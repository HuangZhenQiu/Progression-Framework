package edu.uci.eecs.wukong.framework.mptn;

import edu.uci.eecs.wukong.framework.model.MPTNPackage;

public interface MPTNMessageListener {
	public void onMessage(MPTNPackage bytes);
}
