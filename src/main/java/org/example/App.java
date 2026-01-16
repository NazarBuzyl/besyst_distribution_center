package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.example.model.*;
import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.employee.Dropper;
import org.example.model.employee.Sorter;
import org.example.model.BeltToSortingIntake;
import org.example.model.ReceivingStation;
import org.example.model.ReceivingStationObserver;
import org.example.model.TransportInput;
import org.example.model.TransportObserver;
import org.example.view.ReceivingStationView;
import org.example.view.SortingStationView;
import org.example.view.TransportSection;
import org.example.view.conveyorBelt.ConveyorBeltView;

import org.example.model.employee.Dispatcher;
import org.example.model.BeltToWarehouseIntake;
import org.example.model.warehouse.WarehouseBuffer;
import org.example.model.warehouse.Zone;


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

        SortingStationObserver sortingStationObserver = new SortingStationObserver();
        SortingRoom sortingRoom = new SortingRoom(sortingStationObserver);

        ConveyorBeltArray outputBelts = new ConveyorBeltArray("OUT", 4);
        ConveyorBeltView outputBeltsView = new ConveyorBeltView(outputBelts);

        SortingManager manager = new SortingManager(sortingRoom, outputBelts);

        SortingStationView sortingStationView = new SortingStationView(sortingRoom, sortingStationObserver, manager);

        TransportSection transportSection = initTransportSection(receivingStation);
        ReceivingStationView receivingStationView = new ReceivingStationView(receivingStation, receivingStationObserver);

        // --------- 1) Eingangsbänder (3 Stück) ---------
        ConveyorBeltArray inputBelts = new ConveyorBeltArray("IN", 3);
        ConveyorBeltView inputBeltsView = new ConveyorBeltView(inputBelts);

        // Dropper erzeugt Pakete + legt sie auf die Eingangsbänder
        Dropper dropper = new Dropper(1, inputBelts, receivingStation);
        dropper.setDaemon(true);
        dropper.start();

        // --------- 2) Intake: Ende der Eingangsbänder -> SortingRoom ---------
        BeltToSortingIntake intake1 = new BeltToSortingIntake(101, 1, inputBelts, sortingRoom);
        BeltToSortingIntake intake2 = new BeltToSortingIntake(102, 2, inputBelts, sortingRoom);
        BeltToSortingIntake intake3 = new BeltToSortingIntake(103, 3, inputBelts, sortingRoom);

        intake1.setDaemon(true);
        intake2.setDaemon(true);
        intake3.setDaemon(true);

        intake1.start();
        intake2.start();
        intake3.start();


        // --------- 4) Sorter: SortingRoom -> outputBelts ---------
        int SORTER_COUNT = 0;
        for (int i = 0; i < SORTER_COUNT; i++) {
            int sorterId = 201 + i;
            Sorter sorter = new Sorter(sorterId, outputBelts, sortingRoom);
            sorter.setDaemon(true);
            sorter.start();
        }

        WarehouseBuffer wb1 = new WarehouseBuffer(Zone.OUT_1, 200, 10);
        WarehouseBuffer wb2 = new WarehouseBuffer(Zone.OUT_2, 200, 10);
        WarehouseBuffer wb3 = new WarehouseBuffer(Zone.OUT_3, 200, 10);
        WarehouseBuffer wb4 = new WarehouseBuffer(Zone.OUT_4, 200, 10);

// Output-Band -> WarehouseBuffer
        new BeltToWarehouseIntake(401, 1, outputBelts, wb1).start();
        new BeltToWarehouseIntake(402, 2, outputBelts, wb2).start();
        new BeltToWarehouseIntake(403, 3, outputBelts, wb3).start();
        new BeltToWarehouseIntake(404, 4, outputBelts, wb4).start();

// WarehouseBuffer -> Dispatcher (Batch Versand)
        Dispatcher d1 = new Dispatcher(wb1, 2000);
        Dispatcher d2 = new Dispatcher(wb2, 2000);
        Dispatcher d3 = new Dispatcher(wb3, 2000);
        Dispatcher d4 = new Dispatcher(wb4, 2000);
        d1.setDaemon(true); d2.setDaemon(true); d3.setDaemon(true); d4.setDaemon(true);
        d1.start(); d2.start(); d3.start(); d4.start();


        // --------- UI ---------
        HBox root = new HBox(
                transportSection,
                receivingStationView,
                inputBeltsView,
                sortingStationView,
                outputBeltsView
        );
        root.setLayoutX(30);

        var scene = new Scene(root, 1800, 480);
        stage.setScene(scene);
        stage.setTitle("Distribution Center Simulation");
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("JavaFX Application shutting down.");
    }

    private TransportSection initTransportSection(ReceivingStation receivingStation) {
        List<TransportObserver> transportObservers = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            transportObservers.add(new TransportObserver());
        }

        List<TransportInput> transportInputs = new ArrayList<>();
        transportInputs.add(new TransportInput(transportObservers.get(0), 1, receivingStation, 30000, 100));
        transportInputs.add(new TransportInput(transportObservers.get(1), 2, receivingStation, 5000, 10));
        transportInputs.add(new TransportInput(transportObservers.get(2), 3, receivingStation, 60000, 200));
        // transportInputs.add(new TransportInput(transportObservers.get(3), 4, receivingStation, 5000, 20));

        for (TransportInput transportInput : transportInputs) {
            transportInput.start();
        }

        return new TransportSection(transportInputs, transportObservers);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
