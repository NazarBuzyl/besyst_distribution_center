package org.example.model.conveyorBelt;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.example.model.Package;

/**
 * Klasse für ein Fließband
 *
 * @author Finn Kramer
 */
public class ConveyorBelt
{
    // Fließband-Id
    private final String id;

    // Fließband-Kapazität
    public static final int CAPACITY = 6;

    // Paketpositionen
    private final Deque<Float> packagePositions = new LinkedList<>();

    // Echte Paketobjekte (parallel zu packagePositions)
    private final List<Package> packageList = new ArrayList<>(CAPACITY);

    // Erzeuger-Verbraucher-System
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore semaWrite = new Semaphore(1);
    private final Semaphore semaRead = new Semaphore(0);


    /**
     * Initialisiert eine Instanz aus einer Fließband-Id.
     *
     * @param id Fließband-Id
     */
    public ConveyorBelt(String id) {
        this.id = id;
    }


    /**
     * Lege ein Paket ab.
     *
     * Diese Methode kann den aufrufenden Thread blockieren.
     *
     * @param employeeId Mitarbeiter-Id
     * @param p Paketobjekt
     * @throws InterruptedException Unterbrochen-Ausnahme
     */
    public void dropPackage(int employeeId, Package p) throws InterruptedException {
        this.semaWrite.acquire();
        this.mutex.acquire();
        try {
            p.setPosition(0F);
            packageList.add(0, p);
            packagePositions.addFirst(0F);
            System.out.println(
                    "Employee " + employeeId +
                            " has dropped a package (PLZ=" + p.getZipCode() + ") on conveyor belt " + this.id + "."
            );
        } finally {
            this.mutex.release();
        }
    }


    /**
     * Hole ein Paket ab.
     *
     * Diese Methode kann den aufrufenden Thread blockieren.
     *
     * @param employeeId Mitarbeiter-Id
     * @return Paketobjekt
     * @throws InterruptedException Unterbrochen-Ausnahme
     */
    public Package pickPackageAndReturn(int employeeId) throws InterruptedException {
        this.semaRead.acquire();
        this.mutex.acquire();
        try {
            Package p = packageList.remove(packageList.size() - 1);
            packagePositions.removeLast();
            System.out.println(
                    "Employee " + employeeId +
                            " has picked a package (PLZ=" + p.getZipCode() + ") from conveyor belt " + this.id + "."
            );
            return p;
        } finally {
            this.mutex.release();
        }
    }


    /**
     * Hole die Paketpositionen.
     *
     * @return Paketpositionen
     */
    public Deque<Float> getPackagePositions() {
        return packagePositions;
    }


    /**
     * Hole den mutex.
     *
     * @return mutex
     */
    public Semaphore getMutex() {
        return mutex;
    }


    /**
     * Hole den semaRead.
     *
     * @return semaRead
     */
    public Semaphore getSemaRead() {
        return semaRead;
    }


    /**
     * Hole den semaWrite.
     *
     * @return semaWrite
     */
    public Semaphore getSemaWrite() {
        return semaWrite;
    }

    public List<Package> getPackageList() {
        return packageList;
    }

    public String getId() {
        return id;
    }
}
