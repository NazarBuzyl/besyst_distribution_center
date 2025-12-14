package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.model.conveyorBelt.ConveyorBelt;
import org.example.model.conveyorBelt.ConveyorBeltDriver;
import org.example.model.employee.Dropper;
import org.example.model.employee.Sorter;
import org.example.view.conveyorBelt.ConveyorBeltView;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

        // --- Domain ---
        ConveyorBelt belt = new ConveyorBelt(1);
        ConveyorBeltDriver driver = new ConveyorBeltDriver(belt);
        Dropper dropper = new Dropper(1, belt);
        Sorter sorter = new Sorter(2, belt);

        // --- Threads ---
        driver.setDaemon(true);
        dropper.setDaemon(true);
        sorter.setDaemon(true);

        driver.start();
        dropper.start();
        sorter.start();

        // --- GUI ---
        ConveyorBeltView beltView = new ConveyorBeltView(belt);

        Scene scene = new Scene(new StackPane(beltView), 640, 200);
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
