package org.example.model.warehouse;

import org.example.model.Package;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WarehouseBuffer {

    private final Zone zone;
    private final int capacity;
    private final int dispatchThreshold;
    private final BlockingQueue<Package> storage;

    public WarehouseBuffer(Zone zone, int capacity, int dispatchThreshold) {
        this.zone = zone;
        this.capacity = capacity;
        this.dispatchThreshold = dispatchThreshold;
        this.storage = new LinkedBlockingQueue<>(capacity);
    }

    public Zone getZone() {
        return zone;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getDispatchThreshold() {
        return dispatchThreshold;
    }

    public void store(Package p) throws InterruptedException {
        storage.put(p);
    }

    public int size() {
        return storage.size();
    }

    public List<Package> takeBatchIfAvailable() throws InterruptedException {
        if (storage.size() < dispatchThreshold) {
            return Collections.emptyList();
        }

        List<Package> batch = new ArrayList<>(dispatchThreshold);
        storage.drainTo(batch, dispatchThreshold);

        if (batch.size() < dispatchThreshold) {
            for (Package p : batch) {
                storage.put(p);
            }
            return Collections.emptyList();
        }

        return batch;
    }
}
