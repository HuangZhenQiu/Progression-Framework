package edu.uci.eecs.wukong.framework.metrics;


import edu.uci.eecs.wukong.framework.metrics.Reservoir;
import edu.uci.eecs.wukong.framework.metrics.Snapshot;
import edu.uci.eecs.wukong.framework.util.Clock;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An implemented {@link Reservoir} used to store values that appear in a
 * sliding time window
 */
public class SlidingTimeWindowReservoir implements Reservoir {

  /**
   * default collision buffer
   */
  private static final int DEFAULT_TIME_COLLISION_BUFFER = 1;

  /**
   * Run {@link #removeExpireValues} once every this amount of {@link #update}s
   */
  private static final int REMOVE_IN_UPDATE_THRESHOLD = 256;

  /**
   * default window size
   */
  private static final int DEFAULT_WINDOW_SIZE_MS = 300000;

  /**
   * Allow this amount of values to have the same updating time.
   */
  private final int collisionBuffer;

  /**
   * Size of the window. The unit is millisecond. It is as
   * <code>collisionBuffer</code> times big as the original window size.
   */
  private final long windowMs;

  /**
   * A concurrent map (value's updating time -> value)
   */
  private final ConcurrentSkipListMap<Long, Long> storage;

  /**
   * Total number of values updated in the reservoir.
   */
  private final AtomicLong count;

  /**
   * Updating time of the last value.
   */
  private final AtomicLong lastUpdatingTime;

  private final Clock clock;

  /**
   * Default constructor using default window size
   */
  public SlidingTimeWindowReservoir() {
    this(DEFAULT_WINDOW_SIZE_MS, new Clock() {
      public long currentTimeMillis() {
        return System.currentTimeMillis();
      }
    });
  }

  /**
   * Construct the SlidingTimeWindowReservoir with window size
   *
   * @param windowMs the size of the window. unit is millisecond.
   */
  public SlidingTimeWindowReservoir(long windowMs) {
    this(windowMs, new Clock() {
      public long currentTimeMillis() {
        return System.currentTimeMillis();
      }
    });
  }

  public SlidingTimeWindowReservoir(long windowMs, Clock clock) {
    this(windowMs, DEFAULT_TIME_COLLISION_BUFFER, clock);
  }

  public SlidingTimeWindowReservoir(long windowMs, int collisionBuffer, Clock clock) {
    this.windowMs = windowMs * collisionBuffer;
    this.storage = new ConcurrentSkipListMap<Long, Long>();
    this.count = new AtomicLong();
    this.lastUpdatingTime = new AtomicLong();
    this.clock = clock;
    this.collisionBuffer = collisionBuffer;
  }

  @Override
  public int size() {
    removeExpireValues();
    return storage.size();
  }

  @Override
  public void update(long value) {
    if (count.incrementAndGet() % REMOVE_IN_UPDATE_THRESHOLD == 0) {
      removeExpireValues();
    }
    storage.put(getUpdatingTime(), value);
  }

  /**
   * Remove the values that are earlier than current window
   */
  private void removeExpireValues() {
    storage.headMap(getUpdatingTime() - windowMs).clear();
  }

  /**
   * Return the new updating time. If the new value's system time equals to last
   * value's, use the last updating time + 1 as the new updating time. This
   * operation guarantees all the updating times in the <code>storage</code>
   * strictly increment. No override happens before reaching the
   * <code>collisionBuffer</code>.
   *
   * @return the updating time
   */
  private long getUpdatingTime() {
    while (true) {
      long oldTime = lastUpdatingTime.get();
      long newTime = clock.currentTimeMillis() * collisionBuffer;
      long updatingTime = newTime > oldTime ? newTime : oldTime + 1;
      // make sure the updateTime doesn't overflow to the next millisecond
      if (updatingTime == newTime + collisionBuffer) {
        --updatingTime;
      }
      // make sure no other threads modify the lastUpdatingTime
      if (lastUpdatingTime.compareAndSet(oldTime, updatingTime)) {
        return updatingTime;
      }
    }
  }

  @Override
  public Snapshot getSnapshot() {
    removeExpireValues();
    return new Snapshot(storage.values());
  }
}
