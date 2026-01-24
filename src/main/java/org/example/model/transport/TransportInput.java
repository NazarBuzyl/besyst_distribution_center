package org.example.model.transport;

import org.example.model.Package;
import org.example.model.stations.receiving.ReceivingStation;
import org.example.model.warehouse.Zone;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Nazar Buzyl
 */
public class TransportInput extends Thread {
    private static final int DEFAULT_DELIVERY_TIME = 7500; // 7,5s
    private static final int DEFAULT_DELIVERED_PACKAGES = 20; // unloading -> 5s
    private static final int DEFAULT_UNLOADING_FAKTOR = 250; // ms pro package

    private static final String INTERRUPT_MESSAGE = " Transfer %d was interrupted, but finish last task.%n";
    private static final String UPLOADING_START_MESSAGE = " Transfer %d has started unloading %d packages.%n";
    private static final String UPLOADING_FINISH_MESSAGE = " Transfer %d has finished unloading %d packages.%n";

    private volatile boolean run = true;
    private final int transportId;
    private final ReceivingStation target;

    private final int delivery_time_ms;
    private final int delivered_packages;
    private final int unloading_time;
    private LinkedList<Package> packages;

    // Variable, die Änderungen beim Entladen überwacht und das View-Element darüber benachrichtigt.
    private final TransportObserver transportObserver ;

    public TransportInput(  TransportObserver observer, int id, ReceivingStation target, int delivery_time, int delivered_packages) {
        this.transportObserver = observer;
        this.transportId = id;
        this.target = target;
        this.delivery_time_ms = delivery_time;
        this.delivered_packages = delivered_packages;
        this.unloading_time = delivered_packages*DEFAULT_UNLOADING_FAKTOR;
    }

    public TransportInput( TransportObserver observer, int id, ReceivingStation target) {
        this.transportObserver = observer;
        this.transportId = id;
        this.target = target;
        this.delivery_time_ms = DEFAULT_DELIVERY_TIME;
        this.delivered_packages = DEFAULT_DELIVERED_PACKAGES;
        this.unloading_time =  DEFAULT_DELIVERED_PACKAGES * DEFAULT_UNLOADING_FAKTOR;
    }

    @Override
    public void run() {
        while (run) {
            try {
                this.delivering();
            } catch (InterruptedException e) {
                this.run = false;
                System.err.printf(INTERRUPT_MESSAGE, this.transportId);
            }
        }
    }


    /**
     * Simuliert die Lieferung, wartet auf die Lieferzeit und parkt bei der Ankunft an der Station
     * @throws InterruptedException
     */
    private void delivering() throws InterruptedException {
            this.transportObserver.drive();
            this.packages = this.createNewPackages();
            Thread.sleep(this.delivery_time_ms);
            this.transportObserver.park();
            target.takeParkingPlace(this);
    }

    /**
     * Methode, die für das Entladen von Fahrzeugen verantwortlich ist. Mit Hilfe von Thread.sleep wird der Prozess
     * wie in der Realität simuliert, bei dem Zeit benötigt wird, um ein Fahrzeug zu entladen.
     * Die Entladezeit ist ebenfalls dynamisch und hängt von der Anzahl der Pakete ab.
     *
     * @return Gibt die entladenen Pakete zurück
     * @throws InterruptedException
     */
    public LinkedList<Package> unloading() throws InterruptedException {
            this.transportObserver.startUnloading();
            System.out.printf(LocalTime.now().withNano(0) + UPLOADING_START_MESSAGE, this.transportId, this.delivered_packages);
            Thread.sleep(this.unloading_time);
            System.out.printf(LocalTime.now().withNano(0) + UPLOADING_FINISH_MESSAGE, this.transportId, this.delivered_packages);
            this.transportObserver.finishUnloading();
            return this.packages;
    }

    // Erzeuge echte Package-Objekte mit PLZ und füge sie in die packageQueue ein
    private LinkedList<Package> createNewPackages() {
        LinkedList<Package> newPackages = new LinkedList<>();
        for (int i = 0; i < delivered_packages; i++) {
            Package p = new Package(Zone.randomPlz());
            newPackages.add(p);
        }
        return newPackages;
    }

    public int getDeliveredPackages() {
        return this.delivered_packages;
    }

    public int getDeliveryTime() {
        return this.delivery_time_ms;
    }

    public int getUnloadingTime() {
        return this.unloading_time;
    }

    public int getTransportId() {
        return this.transportId;
    }

    public TransportObserver getTransportObserver() {
        return this.transportObserver;
    }
}
