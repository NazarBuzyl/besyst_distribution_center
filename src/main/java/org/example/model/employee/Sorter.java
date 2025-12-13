package model.employee;


import model.conveyorBelt.ConveyorBelt;
public class Sorter extends Employee
{
    public static final int SORTING_SPEED = 4000;

    private final ConveyorBelt pickTarget;

    // private final ConveyorBelt dropTarget;

    public Sorter(int employeeId, ConveyorBelt pickTarget) // ConveyorBelt dropTarget
    {
        super(employeeId);
        this.pickTarget = pickTarget;
        // this.dropTarget = dropTarget;
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                this.pickTarget.pickPackage(this.getEmployeeId());
                Thread.sleep(SORTING_SPEED);
                // this.dropTarget.dropPackage(this.EmployeeId());
            }
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
