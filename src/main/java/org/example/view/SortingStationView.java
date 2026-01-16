package org.example.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import org.example.model.SortingManager;
import org.example.model.SortingRoom;
import org.example.model.SortingStationObserver;

import java.util.LinkedList;
import java.util.List;

public class SortingStationView extends BorderPane {

    private static final String STATION_NAME = "Sorting zone";

    // Wenn du "beingSorted" + "lastZip" nicht anzeigen willst, sag Bescheid.
    private static final String INFO_TEMPLATE =
            "- Information - \n Waiting: %d/%d \n Being sorted: %d \n Last ZIP: %s";

    private static final int STATION_SIZE = 300;

    private static final int PACKAGE_HEIGHT = 25;
    private static final int PACKAGE_WIDTH = 35;
    private static final int PACKAGE_PER_SEC = 3; // 3x3 = 9 slots wie bei dir

    private final Label stationInfo = new Label();

    private final List<Rectangle> packageViews = new LinkedList<>();
    private final GridPane packagesGrid = new GridPane();

    private final SortingRoom sortingRoom;
    private final SortingManager manager;
    private final SortingStationObserver observer;
    private final int capacity;

    public SortingStationView(SortingRoom sortingRoom, SortingStationObserver observer, SortingManager manager) {
        this.sortingRoom = sortingRoom;
        this.observer = observer;
        this.manager = manager;
        this.capacity = sortingRoom.getCapacity();

        buildTop();
        buildCenter();

        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(1))));
        this.setMinSize(STATION_SIZE, STATION_SIZE);

        // Waiting: Grid/Info updaten
        observer.waitingProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                updatePackages(newVal.doubleValue() / capacity);
                refreshInfo();
            });
        });

        // BeingSorted: nur Info updaten (optional)
        observer.beingSortedProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::refreshInfo);
        });

        // Last ZIP: nur Info updaten (optional)
        observer.lastZipProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::refreshInfo);
        });

        refreshInfo();
    }

    private void buildTop() {
        Label title = new Label(STATION_NAME);

        Button plus = new Button("+");
        Button minus = new Button("-");

        plus.setOnAction(e -> manager.addSorter());
        minus.setOnAction(e -> manager.removeSorter());

        Label sorterCount = new Label();
        sorterCount.textProperty().bind(
                manager.countProperty().asString("Sorter: %d")
        );

        HBox controls = new HBox(10, minus, sorterCount, plus);
        controls.setAlignment(Pos.CENTER);

        VBox infoBox = new VBox(5, title, controls, stationInfo);
        infoBox.setAlignment(Pos.CENTER);

        setTop(infoBox);
    }



    private void buildCenter() {
        int gap = 1;
        packagesGrid.setHgap(gap);
        packagesGrid.setVgap(gap);
        packagesGrid.setAlignment(Pos.BOTTOM_CENTER);
        initPackages();

        StackPane storage = new StackPane(packagesGrid);
        storage.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        storage.setMaxSize(PACKAGE_WIDTH * PACKAGE_PER_SEC + 20, PACKAGE_HEIGHT * PACKAGE_PER_SEC + 20);

        setCenter(storage);
    }

    private void initPackages() {
        packagesGrid.getChildren().clear();
        packageViews.clear();

        for (int i = 0; i < PACKAGE_PER_SEC * PACKAGE_PER_SEC; i++) {

            Rectangle p = createPackage();
            p.setVisible(false);
            packageViews.add(p);

            int row = PACKAGE_PER_SEC - 1 - i / PACKAGE_PER_SEC;
            int col = i % PACKAGE_PER_SEC;

            packagesGrid.add(p, col, row);
        }
    }

    private Rectangle createPackage() {
        Rectangle p = new Rectangle(PACKAGE_WIDTH, PACKAGE_HEIGHT);
        p.setFill(Color.BEIGE);
        p.setStroke(Color.BLACK);
        return p;
    }

    // exakt wie bei dir: fillPercent 0..1 -> Anzahl sichtbarer Rechtecke
    public void updatePackages(double fillPercent) {
        int visiblePackages = (int) Math.round(PACKAGE_PER_SEC * PACKAGE_PER_SEC * fillPercent);

        for (int i = 0; i < packageViews.size(); i++) {
            packageViews.get(i).setVisible(i < visiblePackages);
        }
    }

    private void refreshInfo() {
        int waiting = observer.waitingProperty().get();
        int beingSorted = observer.beingSortedProperty().get();

        int zip = observer.lastZipProperty().get();
        String zipText = (zip <= 0) ? "-" : String.valueOf(zip);

        stationInfo.setText(String.format(INFO_TEMPLATE, waiting, capacity, beingSorted, zipText));
    }
}

