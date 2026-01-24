package org.example.model.employee;

import org.example.model.warehouse.WarehouseBuffer;

public class Dispatcher extends Thread {

    private final WarehouseBuffer warehouseBuffer;
    private final long intervalMs;

    public Dispatcher(WarehouseBuffer warehouseBuffer, long intervalMs) {
        this.warehouseBuffer = warehouseBuffer;
        this.intervalMs = intervalMs;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Thread.sleep(intervalMs);

                System.out.printf(
                        "Dispatcher status: %s (warehouse keeps packages, nothing removed).%n",
                        warehouseBuffer.getZone()
                );
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
