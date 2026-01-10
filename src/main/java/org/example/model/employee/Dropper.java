package org.example.model.employee;

import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.ReceivingStation;
import org.example.model.Package;

public class Dropper extends Employee
{
    public static final int DROP_SPEED = 500; // speed in ms

    private final ConveyorBeltArray target;
    private final ReceivingStation receivingStation;

    public Dropper(int employeeId, ConveyorBeltArray target, ReceivingStation receivingStation)
    {
        super(employeeId);
        this.target = target;
        this.receivingStation = receivingStation;
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                // hole ein Package aus der ReceivingStation (blockiert bis eines verfügbar ist)
                Package p = this.receivingStation.takePackageForDropper();

                // legt auf ein freies Band (blockiert, wenn alle Eingänge voll sind)
                this.target.dropPackage(this.getEmployeeId(), p);

                Thread.sleep(DROP_SPEED);
            }
        }
        catch (InterruptedException e)
        {
            interrupt();
        }
    }
}