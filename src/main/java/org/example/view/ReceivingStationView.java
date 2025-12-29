package org.example.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import org.example.model.ReceivingStation;
import org.example.model.ReceivingStationObserver;

import java.util.LinkedList;
import java.util.List;


public class ReceivingStationView extends BorderPane {
    private static final String STATION_NAME = "Receiving and dispatching zone";
    private static final String INFO_TEMPLATE = "- Information - \n Employees: %d \n Packages: %d/%d";
    private static final int STATION_SIZE = 300;

    private static final int DOOR_HEIGHT = 100;
    private static final int DOOR_WIDTH = 10;
    private static final int LIGHT_SIZE = 10;

    private static final int PACKAGE_HEIGHT = 25;
    private static final int PACKAGE_WIDTH = 35;
    private static final int PACKAGE_PER_SEC = 3;


    private final Label stationInfo = new Label();
    private final Circle accessLight = new Circle(LIGHT_SIZE);
    private final List<Rectangle> packageViews = new LinkedList<>();
    private final GridPane packagesGrid = new GridPane();

    private final ReceivingStation receivingStation;
    private final  ReceivingStationObserver receivingStationObserver;
    private final int storageCapacity;

    public ReceivingStationView(ReceivingStation receivingStation, ReceivingStationObserver receivingStationObserver) {
        this.receivingStation = receivingStation;
        this.receivingStationObserver = receivingStationObserver;
        this.storageCapacity = receivingStation.getStorageCapacity();

        buildTop();
        buildLeft();
        buildCenter();
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        this.setMinSize(STATION_SIZE, STATION_SIZE);

        receivingStationObserver.receivingProperty().addListener((obs, oldVal, newVal) -> {
            changeLight(newVal);
        });

        receivingStationObserver.packagesProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                updatePackages(newVal.doubleValue() / storageCapacity);
                setInformation(3, newVal.intValue());
            });
        });
    }

    private void buildTop() {
        setInformation(3,0);
        VBox infoBox = new VBox(new Label(STATION_NAME), stationInfo);
        infoBox.setAlignment(Pos.CENTER);
        setTop(infoBox);
    }

    private void buildLeft() {
        accessLight.setFill(Color.LIMEGREEN);
        accessLight.setStroke(Color.BLACK);
        Rectangle entrance = new Rectangle(DOOR_WIDTH, DOOR_HEIGHT);
        entrance.setFill(Color.WHITE);
        entrance.setStroke(Color.BLACK);

        VBox box = new VBox(accessLight, entrance);
        box.setAlignment(Pos.CENTER);
        setLeft(box);
        box.setTranslateX(-LIGHT_SIZE);
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

        for (int i = 0; i < PACKAGE_PER_SEC*PACKAGE_PER_SEC; i++) {

            Rectangle p = createPackage();
            p.setVisible(false);
            packageViews.add(p);

            int row = PACKAGE_PER_SEC - 1 - i / PACKAGE_PER_SEC;
            int col = i % PACKAGE_PER_SEC;

            packagesGrid.add(p, col, row);
        }
    }

    private Rectangle createPackage() {
        Rectangle p = new Rectangle( PACKAGE_WIDTH,PACKAGE_HEIGHT);
        p.setFill(Color.BEIGE);
        p.setStroke(Color.BLACK);
        return p;
    }


    public void updatePackages(double fillPercent) {
        int visiblePackages =
                (int) Math.round(PACKAGE_PER_SEC*PACKAGE_PER_SEC * fillPercent);

        for (int i = 0; i < packageViews.size(); i++) {
            packageViews.get(i).setVisible(i < visiblePackages);
        }
    }

    public void setInformation(int employees, int packages) {
        stationInfo.setText(String.format(INFO_TEMPLATE,employees, packages, storageCapacity));
    }

    private void changeLight(boolean isReceiving) {
        if (isReceiving) {
            accessLight.setFill(Color.RED);
        } else  {
            accessLight.setFill(Color.GREEN);
        }
    }
}
