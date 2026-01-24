package org.example.model.conveyorBelt;

import org.example.model.Package;

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
    public ConveyorBeltArray(String id, int arraySize)
    {
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
     * Lege ein Paket auf ein beliebiges freies Band.
     *
     * Diese Methode kann den aufrufenden Thread unterbrechen.
     *
     * @param employeeId Mitarbeiter-Id
     * @throws InterruptedException Unterbrochen-Ausnahme
     */
    public void dropPackage(int employeeId, Package p)
            throws InterruptedException
    {
        ConveyorBelt belt = writableBelts.take();
        belt.dropPackage(employeeId, p);
    }


    /**
     * Lege ein Paket gezielt auf ein bestimmtes Band.
     *
     * @param beltIndex 1-basierter Index
     * @param employeeId Mitarbeiter-Id
     */
    public void dropPackageTo(int beltIndex, int employeeId, Package p) throws InterruptedException {
        if (beltIndex < 1 || beltIndex > belts.size()) {
            throw new IllegalArgumentException("Invalid belt index: " + beltIndex);
        }
        ConveyorBelt belt = belts.get(beltIndex - 1);
        belt.dropPackage(employeeId, p);
    }


    /**
     * Hole ein Paket von einem Band,
     * dessen Ende erreicht wurde.
     *
     * @param employeeId Mitarbeiter-Id
     * @throws InterruptedException Unterbrochen-Ausnahme
     */
    public Package pickPackage(int employeeId)
            throws InterruptedException
    {
        ConveyorBelt belt = readableBelts.take();
        return belt.pickPackageAndReturn(employeeId);
    }



    /**
     * Hole ein Paket gezielt von einem Band.
     *
     * @param beltIndex 1-basierter Index
     * @param employeeId Mitarbeiter-Id
     */
    public Package pickPackageFrom(int beltIndex,
                                   int employeeId)
            throws InterruptedException
    {
        if (beltIndex < 1 || beltIndex > belts.size())
        {
            throw new IllegalArgumentException(
                    "Invalid belt index: " + beltIndex);
        }

        ConveyorBelt belt = belts.get(beltIndex - 1);
        return belt.pickPackageAndReturn(employeeId);
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
    public List<ConveyorBelt> getBelts()
    {
        return Collections.unmodifiableList(new LinkedList<>(belts));
    }
}
