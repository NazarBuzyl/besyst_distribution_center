package org.example.view;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.example.model.transport.TransportInput;
import org.example.model.transport.TransportObserver;

/**
 * @author Nazar Buzyl
 */
public class TruckView extends Group {
    private final static String INFO_TEMPLATE = "- Information -\n %d Packages\n Delivery time: each %ds";
    private final static double DOOR_WIDTH = 5;
    private final static double CARGO_WIDTH = 140;
    private final static double CABIN_SIZE = 40;
    private final static double HEIGHT = 70;
    private final static double WHEEL_RADIUS = 10;
    private final static double PARKING_DURATION = 1000;//ms
    private final static double DOOR_ANIMATION_DURATION = 600;//ms

    private final double totalWidth = DOOR_WIDTH + CARGO_WIDTH + CABIN_SIZE;
    private final double totalHeight = HEIGHT + 2 * WHEEL_RADIUS;

    private Rectangle cargo;
    private Rectangle cabin;
    private Rectangle cargoDoor;
    private Circle rearWheel;
    private Circle frontWheel;
    private Text infoText;
    private HBox stateInfo;
    private LightAccessibility unloadingLight;
    private LightAccessibility transportingLight;

    private Rotate truckRotateY;
    private Rotate doorRotate;

    private boolean unloading = false;

    private final TransportInput transportSystem;
    private final TransportObserver transportObserver;

    public TruckView(TransportInput transportSystem, TransportObserver transportObserver) {
        this.transportSystem = transportSystem;
        this.transportObserver = transportObserver;

        renderTruck();

        transportObserver.unloadingProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                this.unloadingLight.setAccessibility(newVal);// nicht thread-sicher
            });
            if (newVal) {
                // UI-Operationen dürfen nur im JavaFX Application Thread ausgeführt werden.
                Platform.runLater(this::unload); // nicht thread-sicher
            }
        });
        transportObserver.transportingProperty().addListener((obs, oldVal, newVal) -> {
            // UI-Operationen dürfen nur im JavaFX Application Thread ausgeführt werden.
            Platform.runLater(() -> {
                this.transportingLight.setAccessibility(newVal);// nicht thread-sicher
            });
        });
    }

    private void renderTruck() {
        renderCargo();
        renderCargoDoor();
        renderCabin();
        renderInfoText();
        renderWheel();
        renderStateInfo();

        getChildren().addAll(cargo, cargoDoor, cabin, infoText, rearWheel, frontWheel, stateInfo);

        truckRotateY = new Rotate(0, totalWidth / 2, totalHeight / 2, 0, Rotate.Y_AXIS);
        getTransforms().add(truckRotateY);
    }

    private void renderCargoDoor() {
        this.cargoDoor = new Rectangle(DOOR_WIDTH, HEIGHT);
        cargoDoor.setFill(Color.GRAY);
        cargoDoor.setStroke(Color.BLACK);
        cargoDoor.setX(0);
        cargoDoor.setY(0);
        doorRotate = new Rotate(0, DOOR_WIDTH, HEIGHT, 0, Rotate.Z_AXIS);

        cargoDoor.getTransforms().add(doorRotate);
    }

    private void renderCargo() {
        this.cargo = new Rectangle(CARGO_WIDTH, HEIGHT);
        cargo.setFill(Color.LIGHTGRAY);
        cargo.setStroke(Color.BLACK);
        cargo.setX(DOOR_WIDTH);
        cargo.setY(0);
    }

    private void renderInfoText() {
        double distance = 10;
        infoText = new Text(String.format(INFO_TEMPLATE, transportSystem.getDeliveredPackages(), transportSystem.getDeliveryTime() / 1000));
        infoText.setX(distance + DOOR_WIDTH);
        infoText.setY(distance * 2);
    }

    private void renderStateInfo() {
        Text textLight = new Text(String.format("Unloading:%nTransporting:"));
        this.unloadingLight = new LightAccessibility(5, false);
        this.transportingLight = new LightAccessibility(5, true);
        VBox stateLightBox = new VBox(unloadingLight, transportingLight);
        stateLightBox.setSpacing(8);

        this.stateInfo = new HBox(textLight, stateLightBox);
        stateInfo.setLayoutX(DOOR_WIDTH + CARGO_WIDTH);
//        stateInfo.setLayoutY(HEIGHT - CABIN_SIZE);
    }

    private void renderCabin() {
        this.cabin = new Rectangle(CABIN_SIZE, CABIN_SIZE);
        cabin.setFill(Color.DARKGRAY);
        cabin.setStroke(Color.BLACK);
        cabin.setX(DOOR_WIDTH + CARGO_WIDTH);
        cabin.setY(HEIGHT - CABIN_SIZE);
    }

    private void renderWheel() {
        double distance = 20;
        this.rearWheel = new Circle(WHEEL_RADIUS);
        rearWheel.setFill(Color.BLACK);
        double groundY = HEIGHT + WHEEL_RADIUS;

        rearWheel.setCenterX(DOOR_WIDTH + distance);
        rearWheel.setCenterY(groundY);

        this.frontWheel = new Circle(WHEEL_RADIUS);
        frontWheel.setFill(Color.BLACK);

        frontWheel.setCenterX(DOOR_WIDTH + CARGO_WIDTH + CABIN_SIZE - distance);
        frontWheel.setCenterY(groundY);
    }

    // Animation zum Entladen eines LKW
    private void unload() {
        if (unloading) return;
        unloading = true;

        // Animationssequenz zum Entladen eines LKW
        SequentialTransition sequence = new SequentialTransition(
                rotateTruck(180),
                openDoor(),
                new PauseTransition(Duration.millis(transportSystem.getUnloadingTime() - PARKING_DURATION - DOOR_ANIMATION_DURATION * 2)),
                closeDoor(),
                rotateTruck(0)
        );

        sequence.setOnFinished(e -> unloading = false);
        sequence.play();
    }

    private Animation rotateTruck(double angle) {
        return new Timeline(
                new KeyFrame(Duration.millis(PARKING_DURATION),
                        new KeyValue(truckRotateY.angleProperty(), angle)
                )
        );
    }

    private Animation openDoor() {
        return new Timeline(
                new KeyFrame(Duration.millis(DOOR_ANIMATION_DURATION),
                        new KeyValue(doorRotate.angleProperty(), -90)
                )
        );
    }

    private Animation closeDoor() {
        return new Timeline(
                new KeyFrame(Duration.millis(DOOR_ANIMATION_DURATION),
                        new KeyValue(doorRotate.angleProperty(), 0)
                )
        );
    }

    public double getTotalWidth() {
        return totalWidth;
    }

    public double getTotalHeight() {
        return totalHeight;
    }
}