package org.example.model;

import org.example.model.Package;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class SortingRoom {

    public static final int DEFAULT_CAPACITY = 200;

    private final int capacity;

    private final Semaphore mutex = new Semaphore(1);

    // Producer/Consumer Semaphoren
    private final Semaphore semaWrite; // freie Plätze
    private final Semaphore semaRead;  // vorhandene Pakete

    private final Deque<Package> waitingQueue;
    private final Set<Package> beingSorted = new HashSet<>();

    private final SortingStationObserver observer;

    public SortingRoom(SortingStationObserver observer) {
        this(observer, DEFAULT_CAPACITY);
    }

    public SortingRoom(SortingStationObserver observer, int capacity) {
        this.capacity = capacity;
        this.observer = observer;

        this.semaWrite = new Semaphore(capacity);
        this.semaRead = new Semaphore(0);

        this.waitingQueue = new ArrayDeque<>(capacity);

        // Startwerte fürs UI
        observer.changeWaiting(0);
        observer.changeBeingSorted(0);
        observer.finishPick();
    }

    /**
     * Dropper / Eingang legt Paket in den Sortierraum.
     * Blockiert, wenn voll.
     */
    public void putForSorting(Package p) throws InterruptedException {
        semaWrite.acquire(); // warte auf freien Platz

        mutex.acquire();
        try {
            waitingQueue.addLast(p);
            observer.changeWaiting(waitingQueue.size());
        } finally {
            mutex.release();
        }

        semaRead.release(); // 1 Paket mehr vorhanden
    }

    /**
     * Sorter holt Paket zum Sortieren.
     * Blockiert, wenn leer.
     */
    public Package takeForSorting() throws InterruptedException {
        semaRead.acquire(); // warte bis Paket vorhanden

        mutex.acquire();
        try {
            Package p = waitingQueue.removeFirst();
            observer.startPick(p.getZipCode());      // Paket entnommen
            observer.changeWaiting(waitingQueue.size());
            return p;
        } finally {
            mutex.release();
            semaWrite.release(); // 1 Platz frei
        }
    }

    /**
     * Markiert Paket als "wird gerade sortiert"
     */
    public void addBeingSorted(Package p) throws InterruptedException {
        mutex.acquire();
        try {
            beingSorted.add(p);
            observer.changeBeingSorted(beingSorted.size());
        } finally {
            mutex.release();
        }
    }

    /**
     * Entfernt Paket aus "wird gerade sortiert"
     */
    public void removeBeingSorted(Package p) throws InterruptedException {
        mutex.acquire();
        try {
            beingSorted.remove(p);
            observer.changeBeingSorted(beingSorted.size());
            observer.finishPick(); // Pick/Sort-Phase aus
        } finally {
            mutex.release();
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public int getWaitingCount() throws InterruptedException {
        mutex.acquire();
        try {
            return waitingQueue.size();
        } finally {
            mutex.release();
        }
    }

    public int getBeingSortedCount() throws InterruptedException {
        mutex.acquire();
        try {
            return beingSorted.size();
        } finally {
            mutex.release();
        }
    }
}
