package org.example.model.employee;

import org.example.model.Package;
import org.example.model.warehouse.WarehouseBuffer;

import java.util.List;

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

                // Take a batch only when enough packages are available.
                List<Package> batch = warehouseBuffer.takeBatchIfAvailable();

                if (!batch.isEmpty()) {
                    int n = batch.size();
                    String paketText = (n == 1) ? "1 Paket" : (n + " Pakete");

                    System.out.println(
                            "Der Dispatcher hat "
                                    + paketText
                                    + " entnommen und im Lagerbereich "
                                    + warehouseBuffer.getZone()
                                    + " abgelegt."
                    );
                }
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
