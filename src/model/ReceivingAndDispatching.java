package model;

import java.time.LocalTime;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ReceivingAndDispatching {
    private static final String ACCEPTATION_MESSAGE = " Station has accepted delivery transport %d. %n";
    private static final String CURRENT_STATE_MESSAGE  =" Current storage state: %d. %n";

    private final Semaphore input;
    private final Semaphore output;
    private final AtomicInteger currentStorageState;

    public ReceivingAndDispatching() {
        this.input = new Semaphore(1);
        this.output = new Semaphore(1);
        this.currentStorageState = new AtomicInteger(0);
    }

    public boolean packageAcceptance(TransportProducer transport) throws InterruptedException {
        input.acquire();
        System.out.printf(LocalTime.now().withNano(0)+ACCEPTATION_MESSAGE, transport.getTransportId());

        transport.unloading();
        currentStorageState.addAndGet(transport.getDeliveredPackages());

        System.out.printf(LocalTime.now().withNano(0) + CURRENT_STATE_MESSAGE, this.currentStorageState.get());

        input.release();
        return true;
    }

    public void packageDispatch(int takenPackages) throws InterruptedException, StationEmptyException {
        if (currentStorageState.get() <= 0) {
            throw new StationEmptyException();
        }

        output.acquire();
        currentStorageState.addAndGet(-takenPackages);
        output.release();
    }
}

