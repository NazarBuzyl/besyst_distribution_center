package org.example.controller;

import javafx.beans.property.FloatProperty;
import org.example.model.stations.receiving.ReceivingStation;
import org.example.model.transport.DataStatusTransports;
import org.example.model.transport.TransportInput;
import org.example.model.transport.TransportObserver;
import org.example.view.SectionTransport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TransportsController {
    private final SectionTransport sectionTransport;
    private final DataStatusTransports dataStateTransport = new DataStatusTransports();
    private final Map<TransportInput, TransportObserver> transports;
    private final ReceivingStation receivingStation;

    public TransportsController(ReceivingStation receivingStation) {
        this.sectionTransport = new SectionTransport(this);
        this.transports = new LinkedHashMap<>();
        this.receivingStation = receivingStation;

        createDefaultTransports();
    }

    private void createDefaultTransports() {
        List<TransportInput> transportInputs = new ArrayList<>();

        transportInputs.add(addTransport(1, 30000, 100));
        transportInputs.add(addTransport(2, 5000, 10));
        transportInputs.add(addTransport(3, 60000, 200));
//        transportInputs.add(addTransport(transportObservers.get(3), 4, 5000, 20));
    }

    public TransportInput addTransport(int id, int deliveryTimeMS, int deliveryPackages) {
        TransportObserver transportObserver = new TransportObserver();
        TransportInput transport = new TransportInput(transportObserver, id, receivingStation, deliveryTimeMS, deliveryPackages);
        transport.start();
        dataStateTransport.addDataTransport(deliveryTimeMS/1000, deliveryPackages);
        transports.put(transport, transportObserver);
        sectionTransport.addTransportView(transport, transportObserver);

        return transport;
    }

    public SectionTransport getSectionTransport() {
        return sectionTransport;
    }

    public FloatProperty inputSpeedProperty() {
        return dataStateTransport.inputSpeedProperty();
    }

    public void stop() {
        transports.forEach((transportInput, transportObserver) -> {
            transportInput.interrupt();
        });
    }
}
