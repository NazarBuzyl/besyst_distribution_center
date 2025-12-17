package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.model.conveyorBelt.ConveyorBelt;
import org.example.model.conveyorBelt.ConveyorBeltArray;
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
        ConveyorBeltArray conveyorBeltArray = new ConveyorBeltArray(5);
        Dropper dropper = new Dropper(1, conveyorBeltArray);
        dropper.setDaemon(true);
        dropper.start();

        // --- GUI ---
        ConveyorBeltView beltView = new ConveyorBeltView(conveyorBeltArray);

        Scene scene = new Scene(new StackPane(beltView), 1920, 1080);
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
