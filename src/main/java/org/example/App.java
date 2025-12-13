package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.model.ReceivingStation;
import org.example.model.TransportInput;
import org.example.model.conveyorBelt.ConveyorBelt;
import org.example.model.conveyorBelt.ConveyorBeltDriver;
import org.example.model.employee.Dropper;
import org.example.model.employee.Sorter;

import java.util.LinkedList;
import java.util.List;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        //startNazarTeil();
        //startFineTeil();

        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        var scene = new Scene(new StackPane(label), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    private void startNazarTeil() { // erst nach 7,5s gibt was aus
        ReceivingStation receivingStation = new ReceivingStation();

        List<TransportInput> transporters = new LinkedList<>();
        for(int i=1; i<=4; i++) {
            TransportInput newTransport = new TransportInput(i, receivingStation);
            transporters.add(newTransport);
//            transporters.getLast().start(); todo linked list funktioniert nicht
            newTransport.start();
        }
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
        launch();
    }

}