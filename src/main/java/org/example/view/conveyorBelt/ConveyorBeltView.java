package org.example.view.conveyorBelt;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.model.conveyorBelt.ConveyorBelt;
import org.example.model.conveyorBelt.ConveyorBeltArray;

import java.util.*;

public class ConveyorBeltView extends VBox {

    private static final double BELT_WIDTH = 500;
    private static final double BELT_HEIGHT = 60;
    private static final double PACKAGE_SIZE = 20;
    private static final double SPACING = 20;

    private final ConveyorBeltArray conveyorBeltArray;

    // Zu jedem Band: seine Paket-Rechtecke
    private final Map<ConveyorBelt, List<Rectangle>> packageNodes = new HashMap<>();

    // Zu jedem Band: sein Pane
    private final Map<ConveyorBelt, Pane> beltPanes = new HashMap<>();

    public ConveyorBeltView(ConveyorBeltArray conveyorBeltArray) {
        this.conveyorBeltArray = conveyorBeltArray;

        setSpacing(SPACING);
        setPrefWidth(BELT_WIDTH + 40);

        initBelts();
        startAnimation();
    }

    /* ---------- Initialisierung ---------- */

    private void initBelts() {
        for (ConveyorBelt belt : conveyorBeltArray.getBelts()) {
            Pane beltPane = createBeltPane();
            beltPanes.put(belt, beltPane);
            packageNodes.put(belt, new ArrayList<>());
            getChildren().add(beltPane);
        }
    }

    private Pane createBeltPane() {
        Pane pane = new Pane();
        pane.setPrefSize(BELT_WIDTH + 40, BELT_HEIGHT + 40);

        Rectangle beltRect = new Rectangle(BELT_WIDTH, BELT_HEIGHT);
        beltRect.setFill(Color.DARKGRAY);
        beltRect.setArcHeight(20);
        beltRect.setArcWidth(20);
        beltRect.setLayoutX(20);
        beltRect.setLayoutY(20);

        pane.getChildren().add(beltRect);
        return pane;
    }

    /* ---------- Animation ---------- */

    private void startAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderAllBelts();
            }
        };
        timer.start();
    }

    private void renderAllBelts() {
        for (ConveyorBelt belt : beltPanes.keySet()) {
            renderBelt(belt);
        }
    }

    /* ---------- Rendering eines einzelnen Bands ---------- */

    private void renderBelt(ConveyorBelt belt) {
        Pane pane = beltPanes.get(belt);
        List<Rectangle> nodes = packageNodes.get(belt);

        try {
            belt.getMutex().acquire();

            Deque<Float> positions = belt.getPackagePositions();

            // Anzahl Pakete anpassen
            while (nodes.size() < positions.size()) {
                Rectangle pkg = new Rectangle(PACKAGE_SIZE, PACKAGE_SIZE);
                pkg.setFill(Color.CORNFLOWERBLUE);
                pkg.setArcWidth(6);
                pkg.setArcHeight(6);
                nodes.add(pkg);
                pane.getChildren().add(pkg);
            }

            while (nodes.size() > positions.size()) {
                Rectangle r = nodes.remove(nodes.size() - 1);
                pane.getChildren().remove(r);
            }

            int i = 0;
            for (Float pos : positions) {
                Rectangle pkg = nodes.get(i++);
                double x = 20 + (pos / 100.0) * (BELT_WIDTH - PACKAGE_SIZE);
                double y = 20 + (BELT_HEIGHT - PACKAGE_SIZE) / 2;

                pkg.setLayoutX(x);
                pkg.setLayoutY(y);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            belt.getMutex().release();
        }
    }
}
