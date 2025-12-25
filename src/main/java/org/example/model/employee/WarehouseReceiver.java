package org.example.model.employee;
import org.example.model.conveyorBelt.ConveyorBelt;
import org.example.model.conveyorBelt.Package;
import org.example.model.warehouse.*;
public class WarehouseReceiver extends Employee {

    private final Zone zone;
    private final ConveyorBelt outputBelt;
    private final WarehouseBuffer warehouse;
    private final int receiveSpeedMs;

    public WarehouseReceiver(int employeeId,
                             Zone zone,
                             ConveyorBelt outputBelt,
                             WarehouseBuffer warehouse,
                             int receiveSpeedMs) {
        super(employeeId);
        this.zone = zone;
        this.outputBelt = outputBelt;
        this.warehouse = warehouse;
        this.receiveSpeedMs = receiveSpeedMs;
        setName("Receiver-" + zone + "-" + employeeId);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Package p = outputBelt.pickPackage(getEmployeeId());
                warehouse.store(p);

                System.out.println(getName()
                        + " stored package PLZ=" + p.getZipCode()
                        + " to warehouse " + zone
                        + " (size=" + warehouse.size() + "/" + warehouse.getDispatchThreshold() + "+)");

                Thread.sleep(receiveSpeedMs);
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}

