package edu.uci.eecs.wukong.framework.metrics;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A statistical snapshot of a collection of values
 */
public class Snapshot {
  private final ArrayList<Long> values;
  private final int size;

  public Snapshot(Collection<Long> values) {
    this.values = new ArrayList<Long>(values);
    this.size = this.values.size();
    Collections.sort(this.values);
  }

  /**
   * Get the maximum value in the collection
   *
   * @return maximum value
   */
  public long getMax() {
    if (size == 0) {
      return 0;
    }
    return values.get(size - 1);
  }

  /**
   * Get the minimum value in the collection
   *
   * @return minimum value
   */
  public long getMin() {
    if (size == 0) {
      return 0;
    }
    return values.get(0);
  }

  /**
   * Get the average of the values in the collection
   *
   * @return average value
   */
  public double getAverage() {
    if (size == 0) {
      return 0;
    }
    double sum = 0;
    for (long value : values) {
      sum += value;
    }
    return sum / size;
  }

  /**
   * Get the number of values in the collection
   *
   * @return size of the collection
   */
  public int getSize() {
    return size;
  }

  /**
   * Return the entire list of values
   *
   * @return the list of values
   */
  @SuppressWarnings("unchecked")
  public ArrayList<Long> getValues() {
    return (ArrayList<Long>) values.clone();
  }
}
