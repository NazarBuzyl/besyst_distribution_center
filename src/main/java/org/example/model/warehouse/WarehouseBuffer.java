package org.example.model.warehouse;

import org.example.model.Package;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WarehouseBuffer {

    private final Zone zone;
    private final int dispatchThreshold;
    private final BlockingQueue<Package> storage;

    public WarehouseBuffer(Zone zone, int capacity, int dispatchThreshold) {
        this.zone = zone;
        this.dispatchThreshold = dispatchThreshold;
        this.storage = new LinkedBlockingQueue<>(capacity);
    }

    public Zone getZone() {
        return zone;
    }

    public int getDispatchThreshold() {
        return dispatchThreshold;
    }

    public void store(Package p) throws InterruptedException {
        storage.put(p); // блокується, якщо склад переповнений
    }

    /** Чекає, поки на складі буде >= N, і повертає рівно N пакетів */
    public List<Package> takeBatch() throws InterruptedException {
        List<Package> batch = new ArrayList<>(dispatchThreshold);
        for (int i = 0; i < dispatchThreshold; i++) {
            batch.add(storage.take());
        }
        return batch;
    }

    public int size() {
        return storage.size();
    }
}
