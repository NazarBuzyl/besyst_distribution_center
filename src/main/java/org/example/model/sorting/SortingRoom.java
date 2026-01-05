package org.example.model.sorting;

import org.example.model.conveyorBelt.Package;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * SortingRoom repräsentiert den Raum vor den Förderbändern.
 * Dropper legt unsortierte Pakete hier ab, Sorter entnimmt sie, markiert sie als in Bearbeitung
 * und nach der Sortierung werden sie auf ein Zielband gelegt.
 */
public class SortingRoom {

    private static final int DUMP = 10;
    private final BlockingQueue<Package> unsortedQueue = new LinkedBlockingQueue<>(DUMP);

    private final List<Package> beingSorted = Collections.synchronizedList(new ArrayList<>());

    public void submitUnsorted(Package p) throws InterruptedException {
        unsortedQueue.put(p);
    }

    public Package takeForSorting() throws InterruptedException {
        return unsortedQueue.take();
    }

    public void addBeingSorted(Package p) {
        beingSorted.add(p);
    }

    public void removeBeingSorted(Package p) {
        beingSorted.remove(p);
    }

    public List<Package> getBeingSorted() {
        synchronized (beingSorted) {
            return new ArrayList<>(beingSorted);
        }
    }

    public int getUnsortedSize() {
        return unsortedQueue.size();
    }
}

