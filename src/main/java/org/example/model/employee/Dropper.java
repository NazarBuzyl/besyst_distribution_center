package org.example.model.employee;

import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.conveyorBelt.Package;

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
    public void run() {
        try {
            while (true) {
                int zipCode = generateZipCode();   // новий пакет
                Package p = new Package(zipCode);

                this.target.dropPackage(this.getEmployeeId(), p);

                Thread.sleep(DROP_SPEED);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private int generateZipCode() {
        // Bremen-ish ZIP range (28000–28999)
        return 28000 + (int)(Math.random() * 1000);
    }


}
