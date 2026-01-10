package org.example.model;

import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.warehouse.WarehouseBuffer;

public class BeltToWarehouseIntake extends Thread {

    private final int employeeId;
    private final int beltIndex; // 1..5
    private final ConveyorBeltArray outputBelts;
    private final WarehouseBuffer warehouse;

    public BeltToWarehouseIntake(int employeeId, int beltIndex,
                                 ConveyorBeltArray outputBelts,
                                 WarehouseBuffer warehouse) {
        this.employeeId = employeeId;
        this.beltIndex = beltIndex;
        this.outputBelts = outputBelts;
        this.warehouse = warehouse;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Package p = outputBelts.pickPackageFrom(beltIndex, employeeId); // wartet bis Ende erreicht
                warehouse.store(p); // blockiert wenn Warehouse voll (ok)
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
