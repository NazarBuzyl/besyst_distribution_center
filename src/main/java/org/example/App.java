package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.ReceivingStation;
import org.example.model.ReceivingStationObserver;
import org.example.model.TransportInput;
import org.example.model.TransportObserver;
import org.example.model.conveyorBelt.ConveyorBelt;
import org.example.model.conveyorBelt.ConveyorBeltDriver;
import org.example.model.employee.Dropper;
import org.example.model.employee.Sorter;
import org.example.view.ReceivingStationView;
import org.example.view.TruckView;

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

        ReceivingStationView receivingStationView =  new ReceivingStationView(receivingStation, receivingStationObserver);
        BorderPane truck = new BorderPane(renderTruck(receivingStation) );
        truck.setMinWidth(300);

        HBox root = new HBox(truck, receivingStationView);
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

    // todo - create normal controller for rendering
    private VBox renderTruck(ReceivingStation receivingStation) {
        VBox vbox = new VBox(20);

        List<TransportObserver> transportObservers = new ArrayList<>();
        for(int i = 1; i <= 4; i++) {
            transportObservers.add(new  TransportObserver());
        }

        TransportInput transport1 = new TransportInput(transportObservers.get(0), 1, receivingStation, 30000, 100);
        TransportInput transport2 = new TransportInput(transportObservers.get(1), 2, receivingStation, 5000, 10);
//        TransportInput transport3 = new TransportInput(transportObservers.get(2), 3, receivingStation, 20000, 30);
//        TransportInput transport4 = new TransportInput(transportObservers.get(3), 4, receivingStation, 5000, 20);


        TruckView truck1 = new TruckView(transport1, transportObservers.get(0));
        TruckView truck2 = new TruckView(transport2, transportObservers.get(1));
//        TruckView truck3 = new TruckView(transport3, transportObservers.get(2));
//        TruckView truck4 = new TruckView(transport4, transportObservers.get(3));
        vbox.getChildren().addAll(truck1, truck2);

        transport1.start();
        transport2.start();
//        transport3.start();
//        transport4.start();

        return vbox;
    }

    private void startFineTeil() {
                ConveyorBelt cb = new ConveyorBelt(1);

        Dropper dropper = new Dropper(1, cb);

        Sorter sorter = new Sorter(2, cb);

        ConveyorBeltDriver cbd = new ConveyorBeltDriver(cb);

        cbd.start();
        dropper.start();
        sorter.start();

        try
        {
            cbd.join();
            dropper.join();
            sorter.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}