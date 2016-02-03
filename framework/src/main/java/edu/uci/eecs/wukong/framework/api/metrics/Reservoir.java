package edu.uci.eecs.wukong.framework.api.metrics;

/**
 * A reservoir interface to store, update and display values
 */
public interface Reservoir {
  /**
   * Return the number of values in this reservoir
   *
   * @return the number of values;
   */
  int size();

  /**
   * Update the reservoir with the new value
   *
   * @param value a value to update
   */
  void update(long value);

  /**
   * Return a {@link Snapshot} of this reservoir
   *
   * @return a statistical snapshot of this reservoir
   */
  Snapshot getSnapshot();
}