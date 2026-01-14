package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.controller.TransportsController;
import org.example.model.transport.TransportInput;
import org.example.model.transport.TransportObserver;

import java.util.*;
import java.util.List;

/**
 * @author Nazar Buzyl
 */
public class SectionTransport extends BorderPane {
    private final List<TransportInput> trucks;
    private final List<TransportObserver> truckObservers;
    private final List<TruckView> truckViews = new LinkedList<>();
    private final TransportsController controller;

    private final VBox trucksVBox;
    // --- Input controls ---
    private final Slider timeSlider = new Slider(2, 90, 10);
    private final Slider packagesSlider = new Slider(10, 200, 10);


    public SectionTransport(TransportsController controller) {
        this.trucksVBox = new VBox(20);
        this.trucks = new LinkedList<>();
        this.truckObservers = new LinkedList<>();
        this.controller = controller;

        setMinWidth(350);

        setTop(createInputPane());
        setCenter(trucksVBox);
        trucksVBox.setPadding(new Insets(10));
        setAlignment(trucksVBox, Pos.CENTER);
    }

    public void addTransportView(TransportInput transport, TransportObserver observer) {
        TruckView trackView = new TruckView(transport, observer);
        this.trucksVBox.getChildren().add(trackView);
        this.truckViews.add(trackView);
        this.trucks.add(transport);
        this.truckObservers.add(observer);
    }

    private VBox createInputPane() {

        // Delivery Time
        timeSlider.setShowTickLabels(true);
        timeSlider.setShowTickMarks(true);
        timeSlider.setMajorTickUnit(20);
        timeSlider.setMinorTickCount(3);
        timeSlider.setSnapToTicks(true);

        Label timeValue = new Label("10 s");
        timeSlider.valueProperty().addListener((obs, o, n) ->
                timeValue.setText(n.intValue() + " s")
        );

        // Delivery Packages
        packagesSlider.setShowTickLabels(true);
        packagesSlider.setShowTickMarks(true);
        packagesSlider.setMajorTickUnit(50);
        packagesSlider.setMinorTickCount(4);
        packagesSlider.setSnapToTicks(true);

        Label packagesValue = new Label("10");
        packagesSlider.valueProperty().addListener((obs, o, n) ->
                packagesValue.setText(String.valueOf(n.intValue()))
        );

        Button addTransportButton = new Button("Add Transport");
            addTransportButton.setOnAction(e -> handleAddTransport());

        VBox box = new VBox(8,
                new HBox(10, new Label("Delivery Time (s):"), timeSlider, timeValue),
                new HBox(10, new Label("Delivery Packages:"), packagesSlider, packagesValue),
                addTransportButton
        );

        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5;");

        return box;
    }

    private void handleAddTransport() {
        int deliveryTime = (int) timeSlider.getValue() * 1000;
        int packages = (int) packagesSlider.getValue();

        controller.addTransport(0, deliveryTime, packages);
    }
}
