package org.example.model.conveyorBelt;

import java.util.ArrayList;
<<<<<<< Updated upstream
import java.util.List;
import java.util.concurrent.Semaphore;

public class ConveyorBelt {

    private final int conveyorBeltId;
=======
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
public class ConveyorBelt {

    // Fließband-Id
    private final String id;
>>>>>>> Stashed changes

    public static final int CAPACITY = 5;

    private final List<Package> packageList = new ArrayList<>(CAPACITY);

<<<<<<< Updated upstream
=======
    // Echte Paketobjekte
    private final List<Package> packageList = new ArrayList<>(CAPACITY);

    // Erzeuger-Verbraucher-System
>>>>>>> Stashed changes
    private final Semaphore mutex = new Semaphore(1);
    // semaWrite repräsentiert die Anzahl freier Plätze (anfangs CAPACITY)
    private final Semaphore semaWrite = new Semaphore(CAPACITY);
    // semaRead repräsentiert die Anzahl gefüllter Plätze (anfangs 0)
    private final Semaphore semaRead = new Semaphore(0);
<<<<<<< Updated upstream

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
        this.semaRead.release();
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
=======
    private final Semaphore semaCapacity = new Semaphore(CAPACITY);

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
     * @throws InterruptedException Unterbrochen-Ausnahme
     */
    public void dropPackage(int employeeId) throws InterruptedException {
        this.dropPackage(employeeId, new Package(0));
    }

    /**
     * Lege ein Paket ab (mit Paketobjekt).
     *
     * Diese Methode kann den aufrufenden Thread blockieren.
     *
     * @param employeeId Mitarbeiter-Id
     * @param p Paketobjekt
     * @throws InterruptedException Unterbrochen-Ausnahme
     */
    public void dropPackage(int employeeId, Package p) throws InterruptedException {
        this.semaWrite.acquire();
        this.semaCapacity.acquire();
        this.mutex.acquire();
        try {
            p.setPosition(0F);
            this.packageList.add(0, p);
            this.packagePositions.addFirst(0F);
            System.out.println(
                    "Employee " + employeeId +
                            " has dropped a package (PLZ=" + p.getZipCode() + ") on conveyor belt " + this.id + "."
            );
>>>>>>> Stashed changes
        } finally {
            this.mutex.release();
            this.semaWrite.release();
        }
    }

<<<<<<< Updated upstream
    public List<Package> getPackageList() {
        return this.packageList;
    }

=======
    /**
     * Hole ein Paket ab.
     * <p>
     * Diese Methode kann den aufrufenden Thread blockieren.
     *
     * @param employeeId Mitarbeiter-Id
     * @return
     * @throws InterruptedException Unterbrochen-Ausnahme
     */
    public void pickPackage(int employeeId) throws InterruptedException {
        this.pickPackageAndReturn(employeeId);
    }

    /**
     * Hole ein Paket ab (mit Rückgabe des Paketobjekts).
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
            Package p = this.packageList.remove(this.packageList.size() - 1);
            this.packagePositions.removeLast();
            System.out.println(
                    "Employee " + employeeId +
                            " has picked a package (PLZ=" + p.getZipCode() + ") from conveyor belt " + this.id + "."
            );
            return p;
        } finally {
            this.mutex.release();
            this.semaCapacity.release();
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
>>>>>>> Stashed changes
    public Semaphore getMutex() {
        return mutex;
    }

<<<<<<< Updated upstream
=======
    /**
     * Hole den semaRead.
     *
     * @return semaRead
     */
>>>>>>> Stashed changes
    public Semaphore getSemaRead() {
        return semaRead;
    }

<<<<<<< Updated upstream
=======
    /**
     * Hole den semaWrite.
     *
     * @return semaWrite
     */
>>>>>>> Stashed changes
    public Semaphore getSemaWrite() {
        return semaWrite;
    }

<<<<<<< Updated upstream
    public int getConveyorBeltId() {
        return conveyorBeltId;
=======
    public List<Package> getPackageList() {
        return packageList;
    }

    public String getId() {
        return id;
>>>>>>> Stashed changes
    }
}
