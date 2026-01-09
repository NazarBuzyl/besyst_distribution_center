package org.example.model;

import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.warehouse.WarehouseBuffer;

public class BeltToWarehouseIntake extends Thread {

    private final int employeeId;
    private final int beltIndex;
    private final ConveyorBeltArray outputBelts;
    private final WarehouseBuffer warehouse;

    private volatile boolean running = true;

    public BeltToWarehouseIntake(int employeeId,
                                 int beltIndex,
                                 ConveyorBeltArray outputBelts,
                                 WarehouseBuffer warehouse) {
        this.employeeId = employeeId;
        this.beltIndex = beltIndex;
        this.outputBelts = outputBelts;
        this.warehouse = warehouse;
        setName("BeltToWarehouse-" + beltIndex + "-" + warehouse.getZone());
    }

    public void requestStop() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        try {
            while (running && !isInterrupted()) {
                Package p = outputBelts.pickPackageFrom(beltIndex, employeeId); // wartet bis Ende erreicht
                warehouse.store(p); // blockiert wenn Warehouse voll -> Backpressure ok
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
