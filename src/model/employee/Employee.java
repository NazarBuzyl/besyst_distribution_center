package model.employee;


public class Employee extends Thread
{
    private final int employeeId;

    public Employee(int employeeId)
    {
        this.employeeId = employeeId;
    }

    public int getEmployeeId()
    {
        return this.employeeId;
    }
}
