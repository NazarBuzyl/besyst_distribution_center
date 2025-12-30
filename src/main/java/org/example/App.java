package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.model.stations.receiving.ReceivingStation;
import org.example.model.stations.receiving.ReceivingStationObserver;
import org.example.model.transport.TransportInput;
import org.example.model.transport.TransportObserver;
import org.example.view.ReceivingStationView;
import org.example.view.SectionTransport;

import java.util.ArrayList;
import java.util.List;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        ReceivingStationObserver receivingStationObserver = new ReceivingStationObserver();
        ReceivingStation receivingStation = new ReceivingStation(receivingStationObserver);

        SectionTransport sectionTransport = initTransportSection(receivingStation);
        ReceivingStationView receivingStationView =  new ReceivingStationView(receivingStation, receivingStationObserver);
        HBox root = new HBox(sectionTransport, receivingStationView);
        root.setLayoutX(30);

        var scene = new Scene(root, 1000, 480);
        stage.setScene(scene);
        stage.setTitle("Distribution Center Simulation");
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("JavaFX Application shutting down.");
    }

    private SectionTransport initTransportSection(ReceivingStation receivingStation) {
        List<TransportObserver> transportObservers = new ArrayList<>();
        for(int i = 1; i <= 4; i++) {
            transportObservers.add(new  TransportObserver());
        }
        List<TransportInput> transportInputs = new ArrayList<>();

        transportInputs.add(new TransportInput(transportObservers.get(0), 1, receivingStation, 30000, 100));
        transportInputs.add(new TransportInput(transportObservers.get(1), 2, receivingStation, 5000, 10));
        transportInputs.add(new TransportInput(transportObservers.get(2), 3, receivingStation, 60000, 200));
//        transportInputs.add(new TransportInput(transportObservers.get(3), 4, receivingStation, 5000, 20));

        for(TransportInput transportInput : transportInputs) {
            transportInput.start();
        }

        return new SectionTransport(transportInputs, transportObservers);
    }


    public static void main(String[] args) {
        launch(args);
    }

}