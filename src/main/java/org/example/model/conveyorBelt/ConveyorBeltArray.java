package org.example.model.conveyorBelt;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConveyorBeltArray {

    private final String id;
    private final LinkedList<ConveyorBelt> belts = new LinkedList<>();
    private final LinkedList<ConveyorBeltDriver> drivers = new LinkedList<>();

    private final BlockingQueue<ConveyorBelt> writableBelts = new LinkedBlockingQueue<>();
    private final BlockingQueue<ConveyorBelt> readableBelts = new LinkedBlockingQueue<>();

    public ConveyorBeltArray(String id, int arraySize) {
        this.id = id;
        initBelts(arraySize);
        initDrivers();
    }

    private void initBelts(int arraySize) {
        for (int i = 0; i < arraySize; i++) {
            ConveyorBelt belt = new ConveyorBelt(this.id + "." + (i + 1));
            belts.add(belt);
            writableBelts.add(belt);
        }
    }

    private void initDrivers() {
        for (ConveyorBelt belt : belts) {
            ConveyorBeltDriver driver = new ConveyorBeltDriver(belt, writableBelts, readableBelts);
            driver.setDaemon(true);
            driver.start();
            drivers.add(driver);
        }
    }

    public void dropPackage(int employeeId) throws InterruptedException {
        ConveyorBelt belt = writableBelts.take();
        belt.dropPackage(employeeId);
    }

    public void pickPackage(int employeeId) throws InterruptedException {
        ConveyorBelt belt = readableBelts.take();
        belt.pickPackage(employeeId);
    }

    public List<ConveyorBelt> getBelts() {
        return Collections.unmodifiableList(belts);
    }

}
