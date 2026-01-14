package org.example.controller;

import org.example.model.stations.receiving.ReceivingStation;
import org.example.model.stations.receiving.ReceivingStationObserver;
import org.example.view.ReceivingStationView;

public class ReceivingStationController {
    public final ReceivingStationObserver receivingStationObserver;
    public final ReceivingStation receivingStation;
    public final ReceivingStationView receivingStationView;

    public ReceivingStationController() {
        this.receivingStationObserver = new ReceivingStationObserver();
        this.receivingStation = new ReceivingStation(receivingStationObserver);
        this.receivingStationView = new ReceivingStationView(receivingStation, receivingStationObserver);
    }

    public ReceivingStation getReceivingStation() {
        return receivingStation;
    }

    public ReceivingStationObserver getReceivingStationObserver() {
        return receivingStationObserver;
    }

    public ReceivingStationView getReceivingStationView() {
        return receivingStationView;
    }
}
