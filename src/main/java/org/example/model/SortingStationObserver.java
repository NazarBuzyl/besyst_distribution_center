package org.example.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SortingStationObserver {

    private final BooleanProperty sorting = new SimpleBooleanProperty(false); // "Sorter nimmt gerade"
    private final IntegerProperty waiting = new SimpleIntegerProperty(0);     // Warteschlange im SortingRoom
    private final IntegerProperty beingSorted = new SimpleIntegerProperty(0); // Gerade in Bearbeitung
    private final IntegerProperty lastZip = new SimpleIntegerProperty(0);     // Letzte entnommene PLZ (optional)

    public BooleanProperty sortingProperty() { return sorting; }
    public IntegerProperty waitingProperty() { return waiting; }
    public IntegerProperty beingSortedProperty() { return beingSorted; }
    public IntegerProperty lastZipProperty() { return lastZip; }

    public void changeWaiting(int newValue) { waiting.set(newValue); }
    public void changeBeingSorted(int newValue) { beingSorted.set(newValue); }

    public void startPick(int zip) {
        lastZip.set(zip);
        sorting.set(true);
    }

    public void finishPick() {
        sorting.set(false);
    }
}