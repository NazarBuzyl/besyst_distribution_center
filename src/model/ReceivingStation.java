package model;

import java.time.LocalTime;
import java.util.concurrent.Semaphore;

public class ReceivingStation {
    private static final String ACCEPTATION_MESSAGE = " Station has accepted delivery transport %d. %n";
    private static final String CURRENT_STATE_MESSAGE  =" Current storage state: %d. %n";

    private static final int DEFAULT_MAX_STORAGE = 1000;
    private static final int DEFAULT_MAX_PARKING_PLACES = 1;

    private final Semaphore input; // Semaphor zur Kontrolle der geparkten Fahrzeuge vor der Annahmestation

    private final Semaphore semaWrite;
    private final Semaphore semaRead;
    private final Semaphore mutex;

    private int storage; // todo - List<Package>

    public ReceivingStation() {
        this.input = new Semaphore(DEFAULT_MAX_PARKING_PLACES);
        this.semaWrite = new Semaphore(DEFAULT_MAX_STORAGE);
        this.semaRead = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.storage = 0;
    }

    /**
     *  Diese Methode dient dazu, dass das Fahrzeug, welches Pakete liefern wird, parken kann,
     *  da der Zugang zu verfügbaren Entladeplätzen kontrolliert werden muss. Nach dem Parken beginnt der Prozess der Warenannahme.
     *
     * @param transport
     * @throws InterruptedException
     */
    public void takeParkingPlace(TransportInput transport) throws InterruptedException  {
        input.acquire();
        packageAcceptance(transport);
        input.release();
    }

    /**
     * Diese Methode ist für den vollständigen Prozess der Warenannahme im Lager verantwortlich.
     * Dazu gehören die Dokumentation der Ankunft des Fahrzeugs, das Entladen und der Warentransport zum Lager.
     *
     * @param transport
     * @throws InterruptedException
     */
    private void packageAcceptance(TransportInput transport) throws InterruptedException {
        semaWrite.acquire(transport.getDeliveredPackages());// Überprüft, ob genug Platz für die Entladung der Ware vorhanden ist

        System.out.printf(LocalTime.now().withNano(0)+ACCEPTATION_MESSAGE, transport.getTransportId());
        int packages = transport.unloading(); // Aufruf des Entladeprozesses der Ware (jedes Fahrzeug hat unterschiedliche Entladezeiten)

        mutex.acquire();
        this.storage += packages;
        System.out.printf(LocalTime.now().withNano(0) + CURRENT_STATE_MESSAGE, this.storage);
        mutex.release();
        System.out.println(input.getQueueLength());

        semaRead.release(packages);
    }

    /**
     * Methode zur synchronisierten Entnahme von Ware aus dem Lager
     *
     * @param takenPackages
     * @throws InterruptedException
     */
    public void packageTake(int takenPackages) throws InterruptedException {
        semaRead.acquire(takenPackages);

        mutex.acquire();
        storage-=takenPackages;
        mutex.release();

        semaWrite.release(takenPackages);
    }
}

