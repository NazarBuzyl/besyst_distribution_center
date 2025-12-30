package org.example.model.transport;

import javafx.beans.property.*;

/**
 * @author Nazar Buzyl
 */
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
