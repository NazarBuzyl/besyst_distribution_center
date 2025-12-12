package model;

import java.time.LocalTime;

public class TransportProducer extends Thread {
    private static final int DEFAULT_DELIVERY_TIME = 5000; // 20s
    private static final int DEFAULT_UNLOADING_TIME = 5000; // 5s
    private static final int DEFAULT_DELIVERED_PACKAGES = 20;
    private static final int WAITING_TIME = DEFAULT_DELIVERY_TIME/10;

    private static final String WAITING_MESSAGE = " Delivery station full, waiting...%n";
    private static final String INTERRUPT_MESSAGE = " Transfer %d was interrupted, but continues execution.%n";
    private static final String UPLOADING_START_MESSAGE = " Transfer %d has started unloading %d packages.%n";
    private static final String UPLOADING_FINISH_MESSAGE = " Transfer %d has finished unloading %d packages.%n";
    ;

    private final int transportId;
    private final ReceivingAndDispatching target;

    private final int delivery_time;
    private final int delivered_packages;
    private final int unloading_time;

    public TransportProducer(int id, ReceivingAndDispatching target, int delivery_time, int delivered_packages) {
        this.transportId = id;
        this.target = target;
        this.delivery_time = delivery_time;
        this.delivered_packages = delivered_packages;
        this.unloading_time = delivered_packages/DEFAULT_DELIVERED_PACKAGES*DEFAULT_UNLOADING_TIME;
    }

    public TransportProducer(int id, ReceivingAndDispatching target) {
        this.transportId = id;
        this.target = target;
        this.delivery_time = DEFAULT_DELIVERY_TIME;
        this.delivered_packages = DEFAULT_DELIVERED_PACKAGES;
        this.unloading_time = DEFAULT_UNLOADING_TIME;
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


    private void delivering() throws InterruptedException {
            Thread.sleep(this.delivery_time);
            try {
                target.packageAcceptance(this);
            } catch (InterruptedException e) {

                System.out.println(WAITING_MESSAGE);
                Thread.sleep(WAITING_TIME);
            } finally {

                System.out.println();
            }
    }

    public void unloading() {
        try {
            System.out.printf(LocalTime.now().withNano(0) + UPLOADING_START_MESSAGE, this.transportId, this.delivered_packages);
            Thread.sleep(this.unloading_time);
        } catch (InterruptedException e) {
            System.err.printf(INTERRUPT_MESSAGE, this.transportId);
        } finally {
            System.out.printf(LocalTime.now().withNano(0) + UPLOADING_FINISH_MESSAGE, this.transportId, this.delivered_packages);
        }
    }

    public int getDeliveredPackages() {
        return this.delivered_packages;
    }

    public int getTransportId() {
        return this.transportId;
    }
}
