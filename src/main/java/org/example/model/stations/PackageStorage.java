package org.example.model.stations;

import org.example.model.Package;

import java.util.LinkedList;
import java.util.List;
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
    // Queue für echte Package-Objekte, die vom Dropper auf die Eingangsbänder gelegt werden
    protected final List<Package> packageList;

    public PackageStorage(int storageCapacity) {
        this.semaWrite = new Semaphore(storageCapacity);
        this.semaRead = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.storageCapacity = storageCapacity;
        this.storage = 0;
        this.packageList = new LinkedList<>();
    }

    public PackageStorage() {
        this.semaWrite = new Semaphore(DEFAULT_MAX_STORAGE);
        this.semaRead = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.storageCapacity = DEFAULT_MAX_STORAGE;
        this.storage = 0;
        this.packageList = new LinkedList<>();
    }

    /**
     *
     * @param packages - Pakete, die ins Lager gebracht werden müssen
     * @throws InterruptedException
     */
    public void putPackages(List<Package> packages)  throws InterruptedException {
        int packagesCount = packages.size();
        semaWrite.acquire(packagesCount);

        mutex.acquire();
        this.packageList.addAll(packages);
        this.storage += packagesCount;
        mutex.release();

        semaRead.release(packagesCount);
    };

    /**
     * Methode zur synchronisierten Entnahme von Ware aus dem Lager
     *
     * @throws InterruptedException
     * @return Gibt die abgeholte Pakete zurück
     */
    public Package takePackage()  throws InterruptedException {
        semaRead.acquire();

        mutex.acquire();
        storage-=1;
        Package p = packageList.get(0);
        packageList.remove(0);
        mutex.release();

        semaWrite.release(1);
        return p;
    };

    public int getStorageCapacity() {
        return storageCapacity;
    }
}
