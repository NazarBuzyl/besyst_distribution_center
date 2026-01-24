package org.example.model.employee;

import org.example.model.Package;
import org.example.model.ReceivingStation;
import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.statistics.FlowTimeStatistics;

public class Dropper extends Employee {

    public static final int DROP_SPEED = 500;

    private final ConveyorBeltArray target;
    private final ReceivingStation receivingStation;
    private final FlowTimeStatistics flowStats;

    public Dropper(int employeeId,
                   ConveyorBeltArray target,
                   ReceivingStation receivingStation,
                   FlowTimeStatistics flowStats) {
        super(employeeId);
        this.target = target;
        this.receivingStation = receivingStation;
        this.flowStats = flowStats;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Package p = receivingStation.takePackageForDropper();
                target.dropPackage(getEmployeeId(), p);
                flowStats.markStart(p);
                Thread.sleep(DROP_SPEED);
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
