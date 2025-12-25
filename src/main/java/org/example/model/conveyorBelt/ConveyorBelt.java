package org.example.model.conveyorBelt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ConveyorBelt {

    private final int conveyorBeltId;

    public static final int CAPACITY = 5;

    private final List<Package> packageList = new ArrayList<>(CAPACITY);

    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore semaWrite = new Semaphore(1);
    private final Semaphore semaRead = new Semaphore(0);

    public ConveyorBelt(int conveyorBeltId) {
        this.conveyorBeltId = conveyorBeltId;
    }

    public void dropPackage(int employeeId, Package p) throws InterruptedException {
        this.semaWrite.acquire();
        this.mutex.acquire();
        try {
            p.setPosition(0.0F);
            this.packageList.add(0, p);

            System.out.println("Employee " + employeeId
                    + " has dropped a package (PLZ=" + p.getZipCode() + ") on conveyor belt "
                    + conveyorBeltId + ".");
        } finally {
            this.mutex.release();
        }
    }

    public Package pickPackage(int employeeId) throws InterruptedException {
        this.semaRead.acquire();
        this.mutex.acquire();
        try {
            Package p = this.packageList.remove(this.packageList.size() - 1);

            System.out.println("Employee " + employeeId
                    + " has picked a package (PLZ=" + p.getZipCode() + ") from conveyor belt "
                    + conveyorBeltId + ".");
            return p;
        } finally {
            this.mutex.release();
        }
    }

    public List<Package> getPackageList() {
        return this.packageList;
    }

    public Semaphore getMutex() {
        return mutex;
    }

    public Semaphore getSemaRead() {
        return semaRead;
    }

    public Semaphore getSemaWrite() {
        return semaWrite;
    }

    public int getConveyorBeltId() {
        return conveyorBeltId;
    }
}
