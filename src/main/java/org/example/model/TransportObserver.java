package org.example.model;

import javafx.beans.property.*;

public class TransportObserver {
    private final BooleanProperty unloading = new SimpleBooleanProperty(false);

    public BooleanProperty unloadingProperty() {
        return unloading;
    }

    public void startUnloading() {
        unloading.set(true);
    }

    public void finishUnloading() {
        unloading.set(false);
    }
}