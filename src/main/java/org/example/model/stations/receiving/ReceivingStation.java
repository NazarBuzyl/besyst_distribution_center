package org.example.model.stations.receiving;

import org.example.model.stations.PackageStorage;
import org.example.model.transport.TransportInput;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import org.example.model.warehouse.Zone;
import org.example.model.Package;
/**
 * @author Nazar Buzyl
 */
public class ReceivingStation extends PackageStorage {
    private static final String ACCEPTATION_MESSAGE = " Station has accepted delivery transport %d. %n";
    private static final String CURRENT_STATE_MESSAGE  ="%n Current storage state: %d. %n%n";

    private static final int DEFAULT_MAX_PARKING_PLACES = 1;

    private final Semaphore input; // Semaphor zur Kontrolle der geparkten Fahrzeuge vor der Annahmestation

    private final ReceivingStationObserver receivingStationObserver;

    public ReceivingStation(ReceivingStationObserver observer) {
        super();
        this.receivingStationObserver = observer;
        this.input = new Semaphore(DEFAULT_MAX_PARKING_PLACES);
    }

    public ReceivingStation(ReceivingStationObserver observer, int maxStorage) {
        super(maxStorage);
        this.receivingStationObserver = observer;
        this.input = new Semaphore(DEFAULT_MAX_PARKING_PLACES);
    }

    /**
     *  Diese Methode dient dazu, dass das Fahrzeug, welches Pakete liefern wird, parken kann,
     *  da der Zugang zu verfügbaren Entladeplätzen kontrolliert werden muss. Nach dem Parken beginnt der Prozess der Warenannahme.
     *  Diese Methode ist nur für Fahrtzeuge geeignet.
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
     * Entladen dauert bestimmte Zeit. Diese Methode ist nur für Fahrtzeuge geeignet.
     *
     * @param transport
     * @throws InterruptedException
     */
    private void packageAcceptance(TransportInput transport) throws InterruptedException {
        semaWrite.acquire(transport.getDeliveredPackages());// Überprüft, ob genug Platz für die Entladung der Ware vorhanden ist

        receivingStationObserver.startReceiving(); // Observer
        System.out.printf(LocalTime.now().withNano(0)+ACCEPTATION_MESSAGE, transport.getTransportId());
        LinkedList<Package> packages = transport.unloading(); // Aufruf des Entladeprozesses der Ware (jedes Fahrzeug hat unterschiedliche Entladezeiten)

        int packagesCount = packages.size();
        mutex.acquire();
        this.storage += packagesCount;
        this.packageList.addAll(packages);
        System.out.printf(LocalTime.now().withNano(0) + CURRENT_STATE_MESSAGE, this.storage);
        receivingStationObserver.addToTotalPackage(packagesCount);
        receivingStationObserver.changePackages(this.storage);
        mutex.release();
        receivingStationObserver.finishReceiving(); // Observer

        semaRead.release(packagesCount);
    }

    /**
     * Liefere ein Package für den Dropper. Blockiert, bis ein Package verfügbar ist.
     * Aktualisiert außerdem das interne Storage (über PackageStorage.takePackages).
     *
     * @return nächstes Package
     * @throws InterruptedException
     */
    public Package takePackageForDropper() throws InterruptedException {
        // Warte auf ein reales Package-Objekt
        Package p = super.takePackage();

        // Observer updaten (takePackages hat storage bereits reduziert)
        receivingStationObserver.changePackages(this.storage);

        return p;
    }
}