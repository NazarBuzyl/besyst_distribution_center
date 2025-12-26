package org.example.view;

import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.example.model.TransportInput;
import org.example.model.TransportObserver;

public class TruckView extends Group {
    private final static String INFO_TEMPLATE = "- Information -\n %d Packages\n Delivery time: each %ds";
    private final static double doorWidth = 5;
    private final static double cargoWidth = 140;
    private final static double cabinSize = 40;
    private final static double height = 70;
    private final static double wheelRadius = 10;
    private final static double parkingDuration = 1000;//ms
    private final static double doorAnimationDuration = 600;//ms

    private final double totalWidth = doorWidth + cargoWidth + cabinSize;
    private final double totalHeight = height + 2 * wheelRadius;

    private Rectangle cargo;
    private Rectangle cabin;
    private Rectangle cargoDoor;
    private Circle rearWheel;
    private Circle frontWheel;
    private Text infoText;

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
            if (newVal) {
                unload();
            }
        });
    }

    private void renderTruck() {
        renderCargo();
        renderCargoDoor();
        renderCabin();
        renderInfoText();
        renderWheel();

        getChildren().addAll(cargo, cargoDoor, cabin, infoText, rearWheel, frontWheel);

        truckRotateY = new Rotate(0, totalWidth / 2, totalHeight / 2, 0, Rotate.Y_AXIS);
        getTransforms().add(truckRotateY);
    }

    private void renderCargoDoor() {
        this.cargoDoor = new Rectangle(doorWidth, height);
        cargoDoor.setFill(Color.GRAY);
        cargoDoor.setStroke(Color.BLACK);
        cargoDoor.setX(0);
        cargoDoor.setY(0);
        doorRotate = new Rotate(0, doorWidth, height, 0, Rotate.Z_AXIS);

        cargoDoor.getTransforms().add(doorRotate);
    }

    private void renderCargo() {
        this.cargo = new Rectangle(cargoWidth, height);
        cargo.setFill(Color.LIGHTGRAY);
        cargo.setStroke(Color.BLACK);
        cargo.setX(doorWidth);
        cargo.setY(0);
    }

    private void renderInfoText() {
        double distance = 10;
        infoText = new Text(String.format(INFO_TEMPLATE, transportSystem.getDeliveredPackages(), transportSystem.getDeliveryTime() / 1000));
        infoText.setX(distance + doorWidth);
        infoText.setY(distance * 2);
    }

    private void renderCabin() {
        this.cabin = new Rectangle(cabinSize, cabinSize);
        cabin.setFill(Color.DARKGRAY);
        cabin.setStroke(Color.BLACK);
        cabin.setX(doorWidth + cargoWidth);
        cabin.setY(height - cabinSize);
    }

    private void renderWheel() {
        double distance = 20;
        this.rearWheel = new Circle(wheelRadius);
        rearWheel.setFill(Color.BLACK);
        double groundY = height + wheelRadius;

        rearWheel.setCenterX(doorWidth + distance);
        rearWheel.setCenterY(groundY);

        this.frontWheel = new Circle(wheelRadius);
        frontWheel.setFill(Color.BLACK);

        frontWheel.setCenterX(doorWidth + cargoWidth + cabinSize - distance);
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
                new PauseTransition(Duration.millis(transportSystem.getUnloadingTime() - parkingDuration - doorAnimationDuration * 2)),
                closeDoor(),
                rotateTruck(0)
        );

        sequence.setOnFinished(e -> unloading = false);
        sequence.play();
    }

    private Animation rotateTruck(double angle) {
        return new Timeline(
                new KeyFrame(Duration.millis(parkingDuration),
                        new KeyValue(truckRotateY.angleProperty(), angle)
                )
        );
    }

    private Animation openDoor() {
        return new Timeline(
                new KeyFrame(Duration.millis(doorAnimationDuration),
                        new KeyValue(doorRotate.angleProperty(), -90)
                )
        );
    }

    private Animation closeDoor() {
        return new Timeline(
                new KeyFrame(Duration.millis(doorAnimationDuration),
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
