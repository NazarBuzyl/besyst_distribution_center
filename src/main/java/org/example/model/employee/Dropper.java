package model.employee;


import model.conveyorBelt.ConveyorBelt;
public class Dropper extends Employee
{
    public static final int DROP_SPEED = 2000; // speed in ms

    private final ConveyorBelt target;

    public Dropper(int employeeId, ConveyorBelt target)
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
