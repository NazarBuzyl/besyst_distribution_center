package org.example.model.conveyorBelt;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * @author Finn Kramer
 */
public class ConveyorBelt {

    private final String id;

    public static final int CAPACITY = 5;

    private final Deque<Float> packagePositions = new LinkedList<>();

    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore semaWrite = new Semaphore(1);
    private final Semaphore semaRead = new Semaphore(0);

    public ConveyorBelt(String id) {
        this.id = id;
    }

    public void dropPackage(int employeeId) throws InterruptedException {
        this.mutex.acquire();
        this.semaWrite.acquire();
        try {
            packagePositions.addFirst(0F);
            System.out.println(
                    "Employee " + employeeId +
                            " has dropped a package on conveyor belt " + this.id + "."
            );
        } finally {
            this.mutex.release();
        }
    }

    public void pickPackage(int employeeId) throws InterruptedException {
        this.mutex.acquire();
        this.getSemaRead().acquire();
        try {
            packagePositions.removeLast();
            System.out.println(
                    "Employee " + employeeId +
                            " has picked a package from conveyor belt " + this.id + "."
            );
        } finally {
            this.mutex.release();
        }
    }

    public Deque<Float> getPackagePositions() {
        return packagePositions;
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
}
