package org.example.model.conveyorBelt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConveyorBeltArray {

    private final LinkedList<ConveyorBelt> belts = new LinkedList<>();
    private final LinkedList<ConveyorBeltDriver> drivers = new LinkedList<>();

    private final BlockingQueue<ConveyorBelt> writableBelts = new LinkedBlockingQueue<>();
    private final BlockingQueue<ConveyorBelt> readableBelts = new LinkedBlockingQueue<>();

    // Liste der Pakete, die momentan vom Sorter bearbeitet werden (zur Visualisierung)
    private final List<Package> beingSorted = Collections.synchronizedList(new ArrayList<>());

    public ConveyorBeltArray(int arraySize) {
        initBelts(arraySize);
        initDrivers();
    }

    private void initBelts(int arraySize) {
        for (int i = 0; i < arraySize; i++) {
            ConveyorBelt belt = new ConveyorBelt(i + 1);
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

    public void dropPackage(int employeeId, Package p) throws InterruptedException {
        ConveyorBelt belt = writableBelts.take();
        belt.dropPackage(employeeId, p);
    }

    /**
     * Legt ein Paket gezielt auf das Band mit 1-basierter Indexierung (beltIndex).
     * Wartet thread-sicher auf einen freien Slot des Zielbands (semaWrite) und verwendet
     * das Band-Mutex zum Modifizieren der packageList.
     */
    public void dropPackageTo(int beltIndex, int employeeId, Package p) throws InterruptedException {
        if (beltIndex < 1 || beltIndex > belts.size()) {
            throw new IllegalArgumentException("Invalid belt index: " + beltIndex);
        }
        ConveyorBelt belt = belts.get(beltIndex - 1);

        // Warte auf freien Slot auf dem Zielband
        belt.getSemaWrite().acquire();
        boolean added = false;
        belt.getMutex().acquire();
        try {
            p.setPosition(0.0F);
            belt.getPackageList().add(0, p);
            added = true;
            System.out.println("Employee " + employeeId
                    + " has dropped a package (PLZ=" + p.getZipCode() + ") on conveyor belt "
                    + belt.getConveyorBeltId() + " (direct).");
        } finally {
            belt.getMutex().release();
            if (!added) {
                // Im Fehlerfall den Slot zurückgeben
                belt.getSemaWrite().release();
            }
        }
        // Signalisiere, dass nun ein gefüllter Slot vorhanden ist
        belt.getSemaRead().release();
    }

    public Package pickPackage(int employeeId) throws InterruptedException {
        ConveyorBelt belt = readableBelts.take();
        return belt.pickPackage(employeeId);
    }


    public List<ConveyorBelt> getBelts() {
        return Collections.unmodifiableList(belts);
    }

    // Methoden zur Verwaltung der beingSorted-Liste
    public void addBeingSorted(Package p) {
        beingSorted.add(p);
    }

    public void removeBeingSorted(Package p) {
        beingSorted.remove(p);
    }

    public List<Package> getBeingSorted() {
        synchronized (beingSorted) {
            return new ArrayList<>(beingSorted);
        }
    }

}
