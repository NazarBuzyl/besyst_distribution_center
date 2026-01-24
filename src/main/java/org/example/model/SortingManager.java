package org.example.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.employee.Sorter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SortingManager {
    private final SortingRoom sortingRoom;
    private final ConveyorBeltArray outputBelts;
    private final List<Sorter> sorters = new ArrayList<>();
    private final IntegerProperty count = new SimpleIntegerProperty(0);
    private final AtomicInteger nextId = new AtomicInteger(201);

    public SortingManager(SortingRoom sortingRoom, ConveyorBeltArray outputBelts) {
        this.sortingRoom = sortingRoom;
        this.outputBelts = outputBelts;
    }

    public IntegerProperty countProperty() {
        return count;
    }

    public synchronized void addSorter() {
        int id = nextId.getAndIncrement();
        Sorter s = new Sorter(id, outputBelts, sortingRoom);
        s.setDaemon(true);
        s.start();
        sorters.add(s);
        count.set(sorters.size());
    }

    public synchronized void removeSorter() {
        if (sorters.isEmpty()) return;
        Sorter s = sorters.remove(sorters.size() - 1);
        s.interrupt(); // Sorter sollte auf InterruptedException reagieren und sauber enden
        count.set(sorters.size());
    }

    public synchronized void stopAll() {
        for (Sorter s : sorters) s.interrupt();
        sorters.clear();
        count.set(0);
    }
}
