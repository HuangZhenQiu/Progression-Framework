package edu.uci.eecs.wukong.framework.wkpf;

import edu.uci.eecs.wukong.framework.model.LinkTable;
import edu.uci.eecs.wukong.framework.model.ComponentMap;

public interface RemoteProgrammingListener {
	public void update(LinkTable table, ComponentMap map);
}
