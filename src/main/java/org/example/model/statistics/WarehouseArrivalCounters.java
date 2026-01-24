package org.example.model.statistics;

import java.util.concurrent.atomic.AtomicLong;

public class WarehouseArrivalCounters {

    private final AtomicLong totalArrived = new AtomicLong(0);
    private final AtomicLong[] arrivedPerBelt = new AtomicLong[5];

    public WarehouseArrivalCounters() {
        for (int i = 0; i < arrivedPerBelt.length; i++) {
            arrivedPerBelt[i] = new AtomicLong(0);
        }
    }

    public void incArrived(int beltIndex1to5) {
        if (beltIndex1to5 < 1 || beltIndex1to5 > 5) {
            return;
        }
        totalArrived.incrementAndGet();
        arrivedPerBelt[beltIndex1to5 - 1].incrementAndGet();
    }

    public long getTotalArrived() {
        return totalArrived.get();
    }

    public long getArrivedForBelt(int beltIndex1to5) {
        if (beltIndex1to5 < 1 || beltIndex1to5 > 5) {
            return 0;
        }
        return arrivedPerBelt[beltIndex1to5 - 1].get();
    }
}
