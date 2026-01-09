package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
<<<<<<< Updated upstream
=======
import org.example.model.*;
import org.example.model.conveyorBelt.ConveyorBelt;
>>>>>>> Stashed changes
import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.employee.Dropper;
import org.example.model.employee.Sorter;
<<<<<<< Updated upstream
import org.example.model.sorting.SortingRoom;
=======
import org.example.model.employee.Dispatcher;
import org.example.model.BeltToWarehouseIntake;
import org.example.model.warehouse.WarehouseBuffer;
import org.example.model.warehouse.Zone;
import org.example.view.ReceivingStationView;
import org.example.view.SortingStationView;
import org.example.view.TransportSection;
import org.example.model.sorting.BeltToSortingIntake;
import org.example.view.TruckView;
>>>>>>> Stashed changes
import org.example.view.conveyorBelt.ConveyorBeltView;
import org.example.view.sorting.SortingRoomView;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

<<<<<<< Updated upstream
        // --- Domain ---
        ConveyorBeltArray conveyorBeltArray = new ConveyorBeltArray(5);
        SortingRoom sortingRoom = new SortingRoom();

        Dropper dropper = new Dropper(1, sortingRoom);
        dropper.setDaemon(true);
        dropper.start();

        // Start a Sorter so we can see sorting in the UI
        Sorter sorter = new Sorter(2, conveyorBeltArray, sortingRoom);
        sorter.setDaemon(true);
        sorter.start();

        // --- GUI ---
        ConveyorBeltView beltView = new ConveyorBeltView(conveyorBeltArray);
        SortingRoomView sortingView = new SortingRoomView(sortingRoom);

        HBox root = new HBox(sortingView, beltView);

        Scene scene = new Scene(new StackPane(root), 1200, 800);
=======
        SortingStationObserver sortingStationObserver = new SortingStationObserver();
        SortingRoom sortingRoom = new SortingRoom(sortingStationObserver);
        SortingStationView sortingStationView = new SortingStationView(sortingRoom, sortingStationObserver);

        TransportSection transportSection = initTransportSection(receivingStation);
        ReceivingStationView receivingStationView =  new ReceivingStationView(receivingStation, receivingStationObserver);

        // --------- 1) Eingangsbänder (3 Stück) ---------
        ConveyorBeltArray inputBelts = new ConveyorBeltArray("IN", 3);
        ConveyorBeltView inputBeltsView = new ConveyorBeltView(inputBelts);

        // Dropper erzeugt Pakete + legt sie auf die Eingangsbänder
        Dropper dropper = new Dropper(1, inputBelts);
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

        // --------- 3) Ausgangsbänder (für Sortierergebnisse) ---------
        ConveyorBeltArray outputBelts = new ConveyorBeltArray("OUT", 4);
        ConveyorBeltView outputBeltsView = new ConveyorBeltView(outputBelts);

        // --------- 4) Sorter: SortingRoom -> outputBelts ---------
        Sorter sorter = new Sorter(201, outputBelts, sortingRoom);
        sorter.setDaemon(true);
        sorter.start();

        WarehouseBuffer wb1 = new WarehouseBuffer(Zone.OUT_1, 200, 10);
        WarehouseBuffer wb2 = new WarehouseBuffer(Zone.OUT_2, 200, 10);
        WarehouseBuffer wb3 = new WarehouseBuffer(Zone.OUT_3, 200, 10);
        WarehouseBuffer wb4 = new WarehouseBuffer(Zone.OUT_4, 200, 10);
        WarehouseBuffer wb5 = new WarehouseBuffer(Zone.OUT_5_INVALID, 200, 10);

// Output-Band -> WarehouseBuffer
        new BeltToWarehouseIntake(401, 1, outputBelts, wb1).start();
        new BeltToWarehouseIntake(402, 2, outputBelts, wb2).start();
        new BeltToWarehouseIntake(403, 3, outputBelts, wb3).start();
        new BeltToWarehouseIntake(404, 4, outputBelts, wb4).start();
        new BeltToWarehouseIntake(405, 5, outputBelts, wb5).start();

// WarehouseBuffer -> Dispatcher (Batch Versand)
        Dispatcher d1 = new Dispatcher(wb1, 2000);
        Dispatcher d2 = new Dispatcher(wb2, 2000);
        Dispatcher d3 = new Dispatcher(wb3, 2000);
        Dispatcher d4 = new Dispatcher(wb4, 2000);
        Dispatcher d5 = new Dispatcher(wb5, 2000);
        d1.setDaemon(true); d2.setDaemon(true); d3.setDaemon(true); d4.setDaemon(true); d5.setDaemon(true);
        d1.start(); d2.start(); d3.start(); d4.start(); d5.start();


        // Set up all view elements.
        HBox root = new HBox(
                transportSection,
                receivingStationView,
                inputBeltsView,
                sortingStationView,
                outputBeltsView
        );
        root.setLayoutX(30);

        // Set up scene.
        var scene = new Scene(root, 1800, 480);
>>>>>>> Stashed changes
        stage.setScene(scene);
        stage.setTitle("Distribution Center Simulation");
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("JavaFX Application shutting down.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
