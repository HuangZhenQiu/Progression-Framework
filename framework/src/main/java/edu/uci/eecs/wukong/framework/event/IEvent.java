package edu.uci.eecs.wukong.framework.event;

import java.util.List;

public interface IEvent<T extends Number> {
	public void setData(List<T> data);
	public List<T> getData();
}
