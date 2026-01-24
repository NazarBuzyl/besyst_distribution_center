package org.example.model;

import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.statistics.FlowTimeStatistics;
import org.example.model.statistics.WarehouseArrivalCounters;
import org.example.model.warehouse.WarehouseBuffer;

public class BeltToWarehouseIntake extends Thread {

    private final int employeeId;
    private final int beltIndex;
    private final ConveyorBeltArray outputBelts;
    private final WarehouseBuffer warehouse;
    private final FlowTimeStatistics flowStats;
    private final WarehouseArrivalCounters counters;

    public BeltToWarehouseIntake(int employeeId,
                                 int beltIndex,
                                 ConveyorBeltArray outputBelts,
                                 WarehouseBuffer warehouse,
                                 FlowTimeStatistics flowStats,
                                 WarehouseArrivalCounters counters) {
        this.employeeId = employeeId;
        this.beltIndex = beltIndex;
        this.outputBelts = outputBelts;
        this.warehouse = warehouse;
        this.flowStats = flowStats;
        this.counters = counters;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Package p = outputBelts.pickPackageFrom(beltIndex, employeeId);

                warehouse.store(p);

                if (counters != null) {
                    counters.incArrived(beltIndex);
                }
                if (flowStats != null) {
                    flowStats.markEnd(p);
                }
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
