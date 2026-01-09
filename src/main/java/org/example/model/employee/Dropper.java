package org.example.model.employee;

<<<<<<< Updated upstream
import org.example.model.conveyorBelt.Package;
import org.example.model.sorting.SortingRoom;
=======
import org.example.model.Package;
import org.example.model.conveyorBelt.ConveyorBeltArray;
>>>>>>> Stashed changes
import org.example.model.warehouse.Zone;

public class Dropper extends Employee
{
    public static final int DROP_SPEED = 2000; // speed in ms

    private final SortingRoom target;

    public Dropper(int employeeId, SortingRoom target)
    {
        super(employeeId);
        this.target = target;
    }

    @Override
<<<<<<< Updated upstream
    public void run() {
        try {
            while (true) {
                int zipCode = Zone.randomPlz();
                Package p = new Package(zipCode);

                this.target.submitUnsorted(p);
=======
    public void run()
    {
        try
        {
            while (true)
            {
                int zipCode = Zone.randomPlz();
                Package p = new Package(zipCode);

                this.target.dropPackage(this.getEmployeeId(), p);
>>>>>>> Stashed changes

                Thread.sleep(DROP_SPEED);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
