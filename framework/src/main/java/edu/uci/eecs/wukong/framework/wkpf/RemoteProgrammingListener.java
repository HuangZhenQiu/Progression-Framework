package edu.uci.eecs.wukong.framework.wkpf;

import edu.uci.eecs.wukong.framework.wkpf.Model.LinkTable;
import edu.uci.eecs.wukong.framework.wkpf.Model.ComponentMap;

public interface RemoteProgrammingListener {
	public void update(LinkTable table, ComponentMap map);
}
