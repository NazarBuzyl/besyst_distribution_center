package org.example.model;

import java.time.LocalTime;

public class TransportInput extends Thread {
    private static final int DEFAULT_DELIVERY_TIME = 7500; // 7,5s
    private static final int DEFAULT_DELIVERED_PACKAGES = 20; // unloading -> 5s
    private static final int DEFAULT_UNLOADING_FAKTOR = 250; // ms pro package

    private static final String INTERRUPT_MESSAGE = " Transfer %d was interrupted, but continues execution.%n";
    private static final String UPLOADING_START_MESSAGE = " Transfer %d has started unloading %d packages.%n";
    private static final String UPLOADING_FINISH_MESSAGE = " Transfer %d has finished unloading %d packages.%n";


    private final int transportId;
    private final ReceivingStation target;

    private final int delivery_time;
    private final int delivered_packages; // todo - List<Package>
    private final int unloading_time;

    public TransportInput(int id, ReceivingStation target, int delivery_time, int delivered_packages) {
        this.transportId = id;
        this.target = target;
        this.delivery_time = delivery_time;
        this.delivered_packages = delivered_packages;
        this.unloading_time = delivered_packages*DEFAULT_UNLOADING_FAKTOR;
    }

    public TransportInput(int id, ReceivingStation target) {
        this.transportId = id;
        this.target = target;
        this.delivery_time = DEFAULT_DELIVERY_TIME;
        this.delivered_packages = DEFAULT_DELIVERED_PACKAGES;
        this.unloading_time =  DEFAULT_DELIVERED_PACKAGES * DEFAULT_UNLOADING_FAKTOR;
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.delivering();
            } catch (InterruptedException e) {
                System.err.printf(INTERRUPT_MESSAGE, this.transportId);
            }
        }
    }


    /**
     * Simuliert die Lieferung, wartet auf die Lieferzeit und parkt bei der Ankunft an der Station
     * @throws InterruptedException
     */
    private void delivering() throws InterruptedException {
            Thread.sleep(this.delivery_time);
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
    public int unloading() throws InterruptedException {
            System.out.printf(LocalTime.now().withNano(0) + UPLOADING_START_MESSAGE, this.transportId, this.delivered_packages);
            Thread.sleep(this.unloading_time);
            System.out.printf(LocalTime.now().withNano(0) + UPLOADING_FINISH_MESSAGE, this.transportId, this.delivered_packages);
            return this.delivered_packages;
    }

    public int getDeliveredPackages() {
        return this.delivered_packages;
    }

    public int getTransportId() {
        return this.transportId;
    }
}
