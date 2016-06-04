package edu.uci.eecs.wukong.framework.wkpf;

import edu.uci.eecs.wukong.framework.model.ComponentMap;
import edu.uci.eecs.wukong.framework.model.InitValueTable;
import edu.uci.eecs.wukong.framework.model.LinkTable;

public interface RemoteProgrammingListener {
	public void update(LinkTable table, ComponentMap map, InitValueTable initValues, String appId);
}
