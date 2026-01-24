package org.example.model.statistics;

import org.example.model.Package;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FlowTimeStatistics {

    private final ConcurrentMap<Package, Long> startTimesNano = new ConcurrentHashMap<>();

    private long started = 0;
    private long completed = 0;

    private double sumSeconds = 0.0;
    private double minSeconds = Double.POSITIVE_INFINITY;
    private double maxSeconds = 0.0;

    public void markStart(Package p) {
        if (p == null) return;
        startTimesNano.put(p, System.nanoTime());
        synchronized (this) {
            started++;
        }
    }

    public void markEnd(Package p) {
        if (p == null) return;

        Long t0 = startTimesNano.remove(p);
        if (t0 == null) return;

        double dt = (System.nanoTime() - t0) / 1_000_000_000.0;

        synchronized (this) {
            completed++;
            sumSeconds += dt;
            if (dt < minSeconds) minSeconds = dt;
            if (dt > maxSeconds) maxSeconds = dt;
        }
    }

    public synchronized long getStarted() {
        return started;
    }

    public synchronized long getCompleted() {
        return completed;
    }

    public synchronized double getAvgSeconds() {
        if (completed <= 0) return 0.0;
        return sumSeconds / completed;
    }

    public synchronized double getMinSeconds() {
        if (completed <= 0) return 0.0;
        return minSeconds;
    }

    public synchronized double getMaxSeconds() {
        if (completed <= 0) return 0.0;
        return maxSeconds;
    }
}
