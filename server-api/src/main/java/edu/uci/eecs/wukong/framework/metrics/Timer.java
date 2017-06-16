package edu.uci.eecs.wukong.framework.metrics;

import edu.uci.eecs.wukong.framework.util.Clock;

public class Timer implements Metrics {

  private final String name;
  private final Reservoir reservoir;

  /**
   * Default constructor. It uses {@link SlidingTimeWindowReservoir} as the
   * default reservoir.
   *
   * @param name name of this timer
   */
  public Timer(String name) {
    this(name, new SlidingTimeWindowReservoir());
  }

  /**
   * Construct a {@link Timer} with given window size
   *
   * @param name name of this timer
   * @param windowMs the window size. unit is millisecond
   * @param clock the clock for the reservoir
   */
  public Timer(String name, long windowMs, Clock clock) {
    this(name, new SlidingTimeWindowReservoir(windowMs, clock));
  }

  /**
   * Construct a {@link Timer} with given window size and collision buffer
   *
   * @param name name of this timer
   * @param windowMs the window size. unit is millisecond
   * @param collisionBuffer amount of collisions allowed in one millisecond.
   * @param clock the clock for the reservoir
   */
  public Timer(String name, long windowMs, int collisionBuffer, Clock clock) {
    this(name, new SlidingTimeWindowReservoir(windowMs, collisionBuffer, clock));
  }

  /**
   * Construct a {@link Timer} with given {@link Reservoir}
   *
   * @param name name of this timer
   * @param reservoir the given reservoir
   */
  public Timer(String name, Reservoir reservoir) {
    this.name = name;
    this.reservoir = reservoir;
  }

  /**
   * Add the time duration
   *
   * @param duration time duration
   */
  public void update(long duration) {
    if (duration > 0) {
      reservoir.update(duration);
    }
  }

  /**
   * Get the {@link Snapshot}
   *
   * @return a statistical snapshot
   */
  public Snapshot getSnapshot() {
    return reservoir.getSnapshot();
  }

  @Override
  public void visit(MetricsVisitor visitor) {
    visitor.timer(this);
  }

  /**
   * Get the name of the timer
   *
   * @return name of the timer
   */
  public String getName() {
    return name;
  }
}
