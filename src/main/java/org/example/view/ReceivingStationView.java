package org.example.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import org.example.model.stations.receiving.ReceivingStation;
import org.example.model.stations.receiving.ReceivingStationObserver;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Nazar Buzyl
 */
public class ReceivingStationView extends BorderPane {
    private static final String STATION_NAME = "Receiving and dispatching zone";
    private static final String INFO_TEMPLATE = "- Information - \n Employees: %d \n Packages: %d/%d";
    private static final int STATION_SIZE = 300;

    private static final int DOOR_HEIGHT = 100;
    private static final int DOOR_WIDTH = 10;
    private static final int LIGHT_SIZE = 10;

    private static final int PACKAGES_PER_SEC = 3; // Raketenanzahl pro Zeile im Lager


    private final Label stationInfo = new Label();
    private final Label totalPackageInfo = new Label();
    private final LightAccessibility accessLight = new LightAccessibility(LIGHT_SIZE);
    private final List<PackageView> packageViews = new LinkedList<>();
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
            // UI-Operationen dürfen nur im JavaFX Application Thread ausgeführt werden.
            Platform.runLater(() -> {
                this.accessLight.setAccessibility(!newVal);// nicht thread-sicher
            });
        });

        receivingStationObserver.totalPackagesProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                setTotalPackagesInformation(newVal.intValue());
            });
        });

        receivingStationObserver.packagesProperty().addListener((obs, oldVal, newVal) -> {
            // UI-Operationen dürfen nur im JavaFX Application Thread ausgeführt werden.
            Platform.runLater(() -> {
                updatePackagesFilling(newVal.doubleValue() / storageCapacity);// nicht thread-sicher
                setInformation(3, newVal.intValue());// nicht thread-sicher
            });
        });
    }

    private void buildTop() {
        setInformation(3,0);
        setTotalPackagesInformation(0);
        VBox infoBox = new VBox(new Label(STATION_NAME), stationInfo, totalPackageInfo); // UI-Elementen auf der oberen Seite
        infoBox.setAlignment(Pos.CENTER);
        setTop(infoBox);
    }

    private void buildLeft() {
        Rectangle entrance = new Rectangle(DOOR_WIDTH, DOOR_HEIGHT);
        entrance.setFill(Color.WHITE);
        entrance.setStroke(Color.BLACK);

        VBox box = new VBox(accessLight, entrance); // UI-Elementen auf der linken Seite
        box.setAlignment(Pos.CENTER);
        setLeft(box);
        box.setTranslateX(-LIGHT_SIZE);
    }

    private void buildCenter() {
        int gap = 1;
        packagesGrid.setHgap(gap);
        packagesGrid.setVgap(gap);
        packagesGrid.setAlignment(Pos.BOTTOM_CENTER);
        initPackagesView();

        StackPane storage = new StackPane(packagesGrid);
        storage.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        storage.setMaxSize(PackageView.PACKAGE_WIDTH * PACKAGES_PER_SEC + 20, PackageView.PACKAGE_HEIGHT * PACKAGES_PER_SEC + 20);

        setCenter(storage);
    }

    private void initPackagesView() {
        packagesGrid.getChildren().clear();
        packageViews.clear();

        for (int i = 0; i < PACKAGES_PER_SEC * PACKAGES_PER_SEC; i++) {
            PackageView p = new PackageView();
            p.setVisible(false); // zuerst alle Paketen werden erstellt und unsichtbar gemacht, um UI-Thread nicht überlasten
            packageViews.add(p);

            int row = PACKAGES_PER_SEC - 1 - i / PACKAGES_PER_SEC;
            int col = i % PACKAGES_PER_SEC;

            packagesGrid.add(p, col, row);
        }
    }


    private void updatePackagesFilling(double fillPercent) {
        int visiblePackages =
                (int) Math.round(PACKAGES_PER_SEC * PACKAGES_PER_SEC * fillPercent);

        for (int i = 0; i < packageViews.size(); i++) {
            packageViews.get(i).setVisible(i < visiblePackages);
        }
    }

    public void setInformation(int employees, int packages) {
        stationInfo.setText(String.format(INFO_TEMPLATE,employees, packages, storageCapacity));
    }

    public void setTotalPackagesInformation(int totalPackages) {
        totalPackageInfo.setText(String.format("Total Packages: %d", totalPackages));
    }
}
