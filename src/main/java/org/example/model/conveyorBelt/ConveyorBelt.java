package org.example.model.conveyorBelt;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;


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
    public static final int CAPACITY = 5;

    // Paketpositionen
    private final Deque<Float> packagePositions = new LinkedList<>();

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
     * @throws InterruptedException Unterbrochen-Ausnahme
     */
    public void dropPackage(int employeeId) throws InterruptedException {
        this.semaWrite.acquire();
        this.mutex.acquire();
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


    /**
     * Hole ein Paket ab.
     *
     * Diese Methode kann den aufrufenden Thread blockieren.
     *
     * @param employeeId Mitarbeiter-Id
     * @throws InterruptedException Unterbrochen-Ausnahme
     */
    public void pickPackage(int employeeId) throws InterruptedException {
        this.semaRead.acquire();
        this.mutex.acquire();
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

    public String getId()
    {
        return this.id;
    }
}
