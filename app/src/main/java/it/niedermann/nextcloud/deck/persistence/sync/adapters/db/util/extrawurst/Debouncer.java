package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.extrawurst;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Debouncer <T> {
  private final ScheduledExecutorService sched = Executors.newScheduledThreadPool(1);
  private final ConcurrentHashMap<T, TimerTask> delayedMap = new ConcurrentHashMap<>();
  private final Callback<T> callback;
  private final int interval;

  public Debouncer(Callback<T> c, int interval) {
    this.callback = c;
    this.interval = interval;
  }

  public void call(T key) {
    TimerTask task = new TimerTask(key);

    TimerTask prev;
    do {
      prev = delayedMap.putIfAbsent(key, task);
      if (prev == null)
        sched.schedule(task, interval, TimeUnit.MILLISECONDS);
      // Exit only if new task was added to map, or existing task was extended successfully
    } while (prev != null && !prev.extend());
  }

  public void terminate() {
    sched.shutdownNow();
  }

  public interface Callback<T> {
    void call(T key);
  }

  // The task that wakes up when the wait time elapses
  private class TimerTask implements Runnable {
    private final T key;
    private long dueTime;
    private final Object lock = new Object();

    public TimerTask(T key) {
      this.key = key;
      extend();
    }

    public boolean extend() {
      synchronized (lock) {
        if (dueTime < 0) // Task has been shutdown
          return false;
        dueTime = System.currentTimeMillis() + interval;
        return true;
      }
    }

    public void run() {
      synchronized (lock) {
        long remaining = dueTime - System.currentTimeMillis();
        if (remaining > 0) { // Re-schedule task
          sched.schedule(this, remaining, TimeUnit.MILLISECONDS);
        } else { // Mark as terminated and invoke callback
          dueTime = -1;
          try {
            callback.call(key);
          } finally {
            delayedMap.remove(key);
          }
        }
      }
    }
  }
}