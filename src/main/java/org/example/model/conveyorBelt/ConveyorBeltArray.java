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

    // Fließbänder
    private final LinkedList<ConveyorBelt> belts = new LinkedList<>();

    // Fließbandtreiber
    private final LinkedList<ConveyorBeltDriver> drivers = new LinkedList<>();

    // beschreibbare Fließbänder
    private final BlockingQueue<ConveyorBelt> writableBelts = new LinkedBlockingQueue<>();

    // lesbare Fließbänder
    private final BlockingQueue<ConveyorBelt> readableBelts = new LinkedBlockingQueue<>();


    /**
     * Erzeuge eine Instanz aus einer Fließbandreihen-Id und einer FLießbandanzahl.
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
     * Hole Fließbänder.
     *
     * @return Fließbänder
     */
    public List<ConveyorBelt> getBelts() {
        return Collections.unmodifiableList(belts);
    }
}
