package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.employee.Dropper;
import org.example.model.employee.Sorter;
import org.example.model.sorting.SortingRoom;
import org.example.view.conveyorBelt.ConveyorBeltView;
import org.example.view.sorting.SortingRoomView;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

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
