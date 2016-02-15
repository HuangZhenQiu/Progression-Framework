package edu.uci.eecs.wukong.framework.api;

import java.util.List;

/**
 * Used for some feature extraction operator to process fixed size of data.
 * Once the size of data hit the threshold, the function will be called.
 * 
 * @param <T>
 */
public interface Windowable<T> {

	public void window(List<T> data);
}
