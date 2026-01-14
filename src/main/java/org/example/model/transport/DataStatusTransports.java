package org.example.model.transport;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

public class DataStatusTransports {
    private float totalSpeed = 0;
    private final FloatProperty inputSpeed = new SimpleFloatProperty(0);

    public void addDataTransport(int newTime, int newPackages) {
        totalSpeed += (float)newPackages/(float)newTime;
        inputSpeed.setValue(totalSpeed);
    }

    public FloatProperty inputSpeedProperty() {
        return inputSpeed;
    }
}
