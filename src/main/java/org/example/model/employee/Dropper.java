package org.example.model.employee;

import org.example.model.conveyorBelt.ConveyorBeltArray;

public class Dropper extends Employee
{
    public static final int DROP_SPEED = 2000; // speed in ms

    private final ConveyorBeltArray target;

    public Dropper(int employeeId, ConveyorBeltArray target)
    {
        super(employeeId);
        this.target = target;
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                this.target.dropPackage(this.getEmployeeId());
                Thread.sleep(DROP_SPEED);
            }
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

}
