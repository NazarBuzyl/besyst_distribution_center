package org.example.model.employee;

import org.example.model.conveyorBelt.Package;
import org.example.model.warehouse.*;

import java.util.List;

public class Dispatcher extends Thread {

    private final WarehouseBuffer warehouse;
    private final int dispatchTimeMs;

    public Dispatcher(WarehouseBuffer warehouse, int dispatchTimeMs) {
        this.warehouse = warehouse;
        this.dispatchTimeMs = dispatchTimeMs;
        setName("Dispatcher-" + warehouse.getZone());
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                List<Package> batch = warehouse.takeBatch(); // блокується, поки не буде N

                System.out.println(getName()
                        + " dispatched batch for " + warehouse.getZone()
                        + " size=" + batch.size());

                Thread.sleep(dispatchTimeMs);
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}

