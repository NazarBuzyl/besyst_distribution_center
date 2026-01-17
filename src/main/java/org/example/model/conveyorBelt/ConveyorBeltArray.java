package org.example.model.conveyorBelt;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Klasse für eine Fließbandreihe
 *
 * @author Finn Kramer
 */
public class ConveyorBeltArray
{
    // Fließbandreihen-Id
    private final String id;

    // Fließbänder (synchronized access needed)
    private final LinkedList<ConveyorBelt> belts = new LinkedList<>();

    // Fließbandtreiber (synchronized access needed)
    private final LinkedList<ConveyorBeltDriver> drivers = new LinkedList<>();

    // beschreibbare Fließbänder
    private final BlockingQueue<ConveyorBelt> writableBelts = new LinkedBlockingQueue<>();

    // lesbare Fließbänder
    private final BlockingQueue<ConveyorBelt> readableBelts = new LinkedBlockingQueue<>();


    /**
     * Erzeuge eine Instanz aus einer Fließbandreihen-Id und einer Fließbandanzahl.
     *
     * @param id Fließbandreihen-Id
     * @param arraySize Fließbandanzahl
     */
    public ConveyorBeltArray(String id, int arraySize) {
        this.id = id;
        initBelts(arraySize);
        initDrivers();
    }


    /**
     * Initialisiere Fließbänder.
     *
     * @param arraySize Fließbandanzahl
     */
    private void initBelts(int arraySize) {
        for (int i = 0; i < arraySize; i++) {
            ConveyorBelt belt = new ConveyorBelt(this.id + "." + (i + 1));
            belts.add(belt);
            writableBelts.add(belt);
        }
    }


    /**
     * Initialisiere Fließbandtreiber.
     */
    private void initDrivers() {
        for (ConveyorBelt belt : belts) {
            ConveyorBeltDriver driver = new ConveyorBeltDriver(belt, writableBelts, readableBelts);
            driver.setDaemon(true);
            driver.start();
            drivers.add(driver);
        }
    }


    /**
     * Lege ein Paket ab.
     *
     * Diese Methode kann den aufrufenden Thread unterbrechen.
     *
     * @param employeeId Mitarbeiter-Id
     * @throws InterruptedException Unterbrochen-Ausnahme
     */
    public void dropPackage(int employeeId) throws InterruptedException {
        ConveyorBelt belt = writableBelts.take();
        belt.dropPackage(employeeId);
    }


    /**
     * Hole ein Paket ab.
     *
     * Diese Methode kann den aufrufenden Thread unterbrechen.
     *
     * @param employeeId Mitarbeiter-Id
     * @throws InterruptedException Unterbrochen-Ausnahme
     */
    public void pickPackage(int employeeId) throws InterruptedException {
        ConveyorBelt belt = readableBelts.take();
        belt.pickPackage(employeeId);
    }

    /**
     * Entferne das letzte Fließband.
     *
     * @return das entfernte Fließband
     */
    public synchronized ConveyorBelt removeBelt() {
        if (belts.isEmpty()) {
            return null;
        }

        ConveyorBelt removedBelt = belts.removeLast();
        ConveyorBeltDriver removedDriver = drivers.removeLast();

        // Entferne das Band aus den Queues
        writableBelts.remove(removedBelt);
        readableBelts.remove(removedBelt);

        // Stoppe den Driver-Thread ordnungsgemäß
        removedDriver.shutdown();

        System.out.println("User removed belt " + removedBelt.getId() + ".");
        return removedBelt;
    }


    /**
     * Füge ein neues Fließband hinzu.
     *
     * @return das neue Fließband
     */
    public synchronized ConveyorBelt addBelt() {
        ConveyorBelt newBelt = new ConveyorBelt(this.id + "." + (belts.size() + 1));
        belts.add(newBelt);
        writableBelts.add(newBelt);

        // Erstelle und starte den neuen Driver
        ConveyorBeltDriver newDriver = new ConveyorBeltDriver(newBelt, writableBelts, readableBelts);
        newDriver.setDaemon(true);
        newDriver.start();
        drivers.add(newDriver);

        System.out.println("User added belt " + newBelt.getId() + ".");
        return newBelt;
    }

    /**
     * Hole Fließbänder.
     *
     * @return Fließbänder
     */
    public synchronized List<ConveyorBelt> getBelts() {
        return Collections.unmodifiableList(new LinkedList<>(belts));
    }
}