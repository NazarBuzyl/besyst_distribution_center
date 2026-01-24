package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.controller.ReceivingStationController;
import org.example.model.stations.receiving.ReceivingStation;
import org.example.model.stations.receiving.ReceivingStationObserver;
import org.example.model.transport.TransportInput;
import org.example.model.transport.TransportObserver;
import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.employee.Dropper;
import org.example.controller.TransportsController;
import org.example.view.ReceivingStationView;
import org.example.view.SectionTransport;
import org.example.view.conveyorBelt.ConveyorBeltView;

import java.util.ArrayList;
import java.util.List;


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

        // Initialize primary conveyor belts.
        ConveyorBeltArray conveyorBelts = new ConveyorBeltArray("1", 3);
        ConveyorBeltView conveyorBeltView = new ConveyorBeltView(conveyorBelts);
        Dropper dropper = new Dropper(1, conveyorBelts);
        dropper.setDaemon(true);
        dropper.start();

        // Set up all view elements.
        HBox root = new HBox(
                transportsController.getSectionTransport(),
                receivingStationController.getReceivingStationView(),
                conveyorBeltView
        );

        // Set up scene.
        var scene = new Scene(root, 1000, 480);
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