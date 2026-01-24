package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.example.controller.ReceivingStationController;
import org.example.controller.TransportsController;
import org.example.model.*;
import org.example.controller.ReceivingStationController;
import org.example.model.stations.receiving.ReceivingStation;
import org.example.model.stations.receiving.ReceivingStationObserver;
import org.example.model.transport.TransportInput;
import org.example.model.transport.TransportObserver;
import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.employee.Dropper;
import org.example.model.employee.Sorter;
import org.example.model.BeltToSortingIntake;
import org.example.view.SortingStationView;
import org.example.controller.TransportsController;
import org.example.view.ReceivingStationView;
import org.example.view.SectionTransport;
import org.example.view.conveyorBelt.ConveyorBeltView;

import org.example.model.employee.Dispatcher;
import org.example.model.BeltToWarehouseIntake;
import org.example.model.warehouse.WarehouseBuffer;
import org.example.model.warehouse.Zone;


/**
 * JavaFX App
 */
public class App extends Application {
    ReceivingStationController receivingStationController;
    TransportsController transportsController;

    @Override
    public void start(Stage stage) {
        this.receivingStationController = new ReceivingStationController();
        this.transportsController = new TransportsController(receivingStationController.getReceivingStation());



        SortingStationObserver sortingStationObserver = new SortingStationObserver();
        SortingRoom sortingRoom = new SortingRoom(sortingStationObserver);

        ConveyorBeltArray outputBelts = new ConveyorBeltArray("OUT", 4);
        ConveyorBeltView outputBeltsView = new ConveyorBeltView(outputBelts);

        SortingManager manager = new SortingManager(sortingRoom, outputBelts);

        SortingStationView sortingStationView = new SortingStationView(sortingRoom, sortingStationObserver, manager);

        // --------- 1) Eingangsbänder (3 Stück) ---------
        ConveyorBeltArray inputBelts = new ConveyorBeltArray("IN", 3);
        ConveyorBeltView inputBeltsView = new ConveyorBeltView(inputBelts);

        // Dropper erzeugt Pakete + legt sie auf die Eingangsbänder
        Dropper dropper = new Dropper(1, inputBelts, receivingStationController.getReceivingStation());
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
                transportsController.getSectionTransport(),
                receivingStationController.getReceivingStationView(),
                inputBeltsView,
                sortingStationView,
                outputBeltsView
        );
        root.setLayoutX(30);
        // Set up scene.
        Scene scene = new Scene(root, 1800, 480);
        stage.setScene(scene);
        stage.setTitle("Distribution Center Simulation");
        stage.show();
    }

    @Override
    public void stop() {
        transportsController.stop();
        System.out.println("JavaFX Application shutting down.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
