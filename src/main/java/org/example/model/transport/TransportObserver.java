package org.example.model.transport;

import javafx.beans.property.*;

/**
 * @author Nazar Buzyl
 */
public class TransportObserver {
    private final BooleanProperty unloading = new SimpleBooleanProperty(false);
    private final BooleanProperty transporting = new SimpleBooleanProperty(false);

    public BooleanProperty unloadingProperty() {
        return unloading;
    }
    public BooleanProperty transportingProperty() { return transporting;}

    public void startUnloading() {
        unloading.set(true);
    }

    public void finishUnloading() {
        unloading.set(false);
    }

    public void drive() {
        transporting.set(true);
    }

    public void park() {
        transporting.set(false);
    }
}
