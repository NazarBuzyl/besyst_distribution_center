package org.example.model.conveyorBelt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ConveyorBelt
{
    private final int conveyorBeltId;

    public static final int CAPACITY = 5; // Package capacity - 1

    private final List<Float> packagePositions = new ArrayList<>(CAPACITY);

    private final Semaphore mutex = new Semaphore(1);

    private final Semaphore semaWrite = new Semaphore(1);

    private final Semaphore semaRead = new Semaphore(0);

    public ConveyorBelt(int conveyorBeltId)
    {
        this.conveyorBeltId = conveyorBeltId;
    }

    public void dropPackage(int employeeId) throws InterruptedException
    {
        this.semaWrite.acquire();
        this.mutex.acquire();
        //this.packagePositions.addFirst(0F); todo - need fix
        System.out.println("Employee " + employeeId + " has dropped a package on conveyor belt " + this.conveyorBeltId + ".");
        this.mutex.release();
    }

    public void pickPackage(int employeeId) throws InterruptedException
    {
        this.semaRead.acquire();
        this.mutex.acquire();
        //this.packagePositions.removeLast(); todo - need fix
        System.out.println("Employee " + employeeId + " has picked a package from conveyor belt " + this.conveyorBeltId + ".");
        this.mutex.release();
    }

    public List<Float> getPackagePositions()
    {
        return this.packagePositions;
    }

    public Semaphore getMutex()
    {
        return this.mutex;
    }

    public Semaphore getSemaRead()
    {
        return semaRead;
    }

    public Semaphore getSemaWrite()
    {
        return semaWrite;
    }
}
