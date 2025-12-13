package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.model.conveyorBelt.ConveyorBelt;
import org.example.model.conveyorBelt.ConveyorBeltDriver;
import org.example.model.employee.Dropper;
import org.example.model.employee.Sorter;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

        // --- Domain-Objekte ---
        ConveyorBelt belt = new ConveyorBelt(1);
        ConveyorBeltDriver driver = new ConveyorBeltDriver(belt);
        Dropper dropper = new Dropper(1, belt);
        Sorter sorter = new Sorter(2, belt);

        // --- Driver-Thread starten ---
        driver.setDaemon(true); // beendet sich automatisch beim Schließen der App
        driver.start();
        dropper.setDaemon(true);
        sorter.setDaemon(true);
        dropper.start();
        sorter.start();

        // --- GUI ---
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var label = new Label(
                "Hello, JavaFX " + javafxVersion +
                        ", running on Java " + javaVersion + ".\n" +
                        "ConveyorBeltDriver is running.\n" +
                        "Check the console output."
        );

        var scene = new Scene(new StackPane(label), 640, 480);
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
