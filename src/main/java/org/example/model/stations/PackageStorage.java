package org.example.model.stations;

import java.util.concurrent.Semaphore;

/**
 * @author Nazar Buzyl
 */
public class PackageStorage {
    protected static final int DEFAULT_MAX_STORAGE = 1000;

    protected final Semaphore semaWrite;
    protected final Semaphore semaRead;
    protected final Semaphore mutex;
    protected final int storageCapacity;

    protected int storage;

    public PackageStorage(int storageCapacity) {
        this.semaWrite = new Semaphore(storageCapacity);
        this.semaRead = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.storageCapacity = storageCapacity;
        this.storage = 0;
    }

    public PackageStorage() {
        this.semaWrite = new Semaphore(DEFAULT_MAX_STORAGE);
        this.semaRead = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.storageCapacity = DEFAULT_MAX_STORAGE;
        this.storage = 0;
    }

    /**
     *
     * @param packages - Pakete, die ins Lager gebracht werden müssen
     * @throws InterruptedException
     */
    public void putPackages(int packages)  throws InterruptedException {
        semaWrite.acquire(packages);

        mutex.acquire();
        this.storage += packages;
        mutex.release();

        semaRead.release(packages);
    };

    /**
     * Methode zur synchronisierten Entnahme von Ware aus dem Lager
     *
     * @param numberOfPackages
     * @throws InterruptedException
     * @return Gibt die abgeholte Pakete zurück
     */
    public int takePackages(int numberOfPackages)  throws InterruptedException {
        semaRead.acquire(numberOfPackages);

        mutex.acquire();
        storage-=numberOfPackages;
        mutex.release();

        semaWrite.release(numberOfPackages);
        return numberOfPackages;
    };

    public int getStorageCapacity() {
        return storageCapacity;
    }
}
