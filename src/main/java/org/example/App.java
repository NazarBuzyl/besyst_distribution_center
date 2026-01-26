package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.example.controller.ReceivingStationController;
import org.example.controller.TransportsController;
import org.example.model.*;
import org.example.controller.ReceivingStationController;
import org.example.model.stations.receiving.ReceivingStation;
import org.example.model.stations.receiving.ReceivingStationObserver;
import org.example.model.statistics.FlowTimeStatistics;
import org.example.model.statistics.WarehouseArrivalCounters;
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
    private static final long UI_UPDATE_NS = 200_000_000L;
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

        // --------- Eingangsbänder (3 Stück) ---------
        ConveyorBeltArray inputBelts = new ConveyorBeltArray("IN", 3);
        ConveyorBeltView inputBeltsView = new ConveyorBeltView(inputBelts);

//         Dropper erzeugt Pakete + legt sie auf die Eingangsbänder
        FlowTimeStatistics flowStats = new FlowTimeStatistics();
        WarehouseArrivalCounters arrivalCounters = new WarehouseArrivalCounters();

// Dropper erzeugt Pakete + legt sie auf die Eingangsbänder
        Dropper dropper = new Dropper(
                1,
                inputBelts,
                receivingStationController.getReceivingStation(),
                flowStats
        );
        dropper.setDaemon(true);
        dropper.start();


        // --------- Intake: Ende der Eingangsbänder -> SortingRoom ---------
        BeltToSortingIntake intake1 = new BeltToSortingIntake(101, 1, inputBelts, sortingRoom);
        BeltToSortingIntake intake2 = new BeltToSortingIntake(102, 2, inputBelts, sortingRoom);
        BeltToSortingIntake intake3 = new BeltToSortingIntake(103, 3, inputBelts, sortingRoom);

        intake1.setDaemon(true);
        intake2.setDaemon(true);
        intake3.setDaemon(true);

        intake1.start();
        intake2.start();
        intake3.start();


        // --------- Sorter: SortingRoom -> outputBelts ---------
        int SORTER_COUNT = 0;
        for (int i = 0; i < SORTER_COUNT; i++) {
            int sorterId = 201 + i;
            Sorter sorter = new Sorter(sorterId, outputBelts, sortingRoom);
            sorter.setDaemon(true);
            sorter.start();
        }

                WarehouseBuffer wb1 = new WarehouseBuffer(Zone.OUT_1, Integer.MAX_VALUE, 10);
                WarehouseBuffer wb2 = new WarehouseBuffer(Zone.OUT_2, Integer.MAX_VALUE, 10);
                WarehouseBuffer wb3 = new WarehouseBuffer(Zone.OUT_3, Integer.MAX_VALUE, 10);
                WarehouseBuffer wb4 = new WarehouseBuffer(Zone.OUT_4, Integer.MAX_VALUE, 10);

// Output-Band -> WarehouseBuffer
        new BeltToWarehouseIntake(401, 1, outputBelts, wb1, flowStats, arrivalCounters).start();
        new BeltToWarehouseIntake(402, 2, outputBelts, wb2, flowStats, arrivalCounters).start();
        new BeltToWarehouseIntake(403, 3, outputBelts, wb3, flowStats, arrivalCounters).start();
        new BeltToWarehouseIntake(404, 4, outputBelts, wb4, flowStats, arrivalCounters).start();

        // WarehouseBuffer -> Dispatcher (Batch Versand)
        Dispatcher d1 = new Dispatcher(wb1, 2000);
        Dispatcher d2 = new Dispatcher(wb2, 2000);
        Dispatcher d3 = new Dispatcher(wb3, 2000);
        Dispatcher d4 = new Dispatcher(wb4, 2000);
        d1.setDaemon(true); d2.setDaemon(true); d3.setDaemon(true); d4.setDaemon(true);
        d1.start(); d2.start(); d3.start(); d4.start();


        VBox statsPanel = buildStatsPanel(flowStats, arrivalCounters);



        // --------- UI ---------
        HBox root = new HBox(
                transportsController.getSectionTransport(),
                receivingStationController.getReceivingStationView(),
                inputBeltsView,
                sortingStationView,
                outputBeltsView,
                statsPanel
        );
        root.setLayoutX(30);

        Group content = new Group(root);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(
                scrollPane,
                bounds.getWidth() * 0.95,
                bounds.getHeight() * 0.9
        );
        stage.setScene(scene);
        stage.setTitle("Distribution Center Simulation");
        stage.setResizable(true);
        stage.show();
    }

    private VBox buildStatsPanel(FlowTimeStatistics flowStats, WarehouseArrivalCounters arrivalCounters) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(14));
        box.setMinWidth(340);
        box.setMaxWidth(340);
        box.setAlignment(Pos.TOP_LEFT);

        box.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: rgba(0,0,0,0.15);" +
                        "-fx-border-width: 1;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;"
        );

        Label title = new Label("Statistics");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        Label inputLabel = new Label();
        Label outputLabel = new Label();
        Label avgLabel = new Label();

        inputLabel.setStyle("-fx-font-size: 12px;");
        outputLabel.setStyle("-fx-font-size: 12px;");
        avgLabel.setStyle("-fx-font-size: 12px;");

        VBox arrivalsCard = new VBox(6);
        arrivalsCard.setPadding(new Insets(10));
        arrivalsCard.setStyle(
                "-fx-background-color: rgba(0,0,0,0.03);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: rgba(0,0,0,0.10);" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        Label arrivalsTitle = new Label("Warehouse arrivals (OUT belts)");
        arrivalsTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        Label totalArrived = new Label();
        totalArrived.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        Label b1 = new Label();
        Label b2 = new Label();
        Label b3 = new Label();
        Label b4 = new Label();

        b1.setStyle("-fx-font-size: 12px;");
        b2.setStyle("-fx-font-size: 12px;");
        b3.setStyle("-fx-font-size: 12px;");
        b4.setStyle("-fx-font-size: 12px;");

        arrivalsCard.getChildren().addAll(
                arrivalsTitle,
                totalArrived,
                b1, b2, b3, b4
        );

        box.getChildren().addAll(
                title,
                timeLabel,
                separator(),
                inputLabel,
                outputLabel,
                avgLabel,
                separator(),
                arrivalsCard
        );

        long startNs = System.nanoTime();

        AnimationTimer timer = new AnimationTimer() {
            private long last = 0;

            @Override
            public void handle(long now) {
                if (now - last < UI_UPDATE_NS) return;

                double sec = (now - startNs) / 1_000_000_000.0;
                timeLabel.setText(String.format("Time: %.1f s", sec));

                long in = flowStats.getStarted();
                long out = flowStats.getCompleted();

                inputLabel.setText("Input total (created): " + in);
                outputLabel.setText("Output total (stored): " + out);
                avgLabel.setText(String.format("Avg flow time: %.3f s", flowStats.getAvgSeconds()));

                long total = arrivalCounters.getTotalArrived();
                totalArrived.setText("Total arrived at warehouse: " + total);

                b1.setText("OUT.1 arrived: " + arrivalCounters.getArrivedForBelt(1));
                b2.setText("OUT.2 arrived: " + arrivalCounters.getArrivedForBelt(2));
                b3.setText("OUT.3 arrived: " + arrivalCounters.getArrivedForBelt(3));
                b4.setText("OUT.4 arrived: " + arrivalCounters.getArrivedForBelt(4));

                last = now;
            }
        };
        timer.start();

        return box;
    }

    private Region separator() {
        Region r = new Region();
        r.setMinHeight(1);
        r.setPrefHeight(1);
        r.setMaxHeight(1);
        r.setStyle("-fx-background-color: rgba(0,0,0,0.10);");
        VBox.setMargin(r, new Insets(6, 0, 6, 0));
        return r;
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
