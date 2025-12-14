package org.example.view.conveyorBelt;


import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.model.conveyorBelt.ConveyorBelt;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ConveyorBeltView extends Pane {

    private static final double BELT_WIDTH = 500;
    private static final double BELT_HEIGHT = 60;
    private static final double PACKAGE_SIZE = 20;

    private final ConveyorBelt belt;
    private final List<Rectangle> packageNodes = new ArrayList<>();

    public ConveyorBeltView(ConveyorBelt belt) {
        this.belt = belt;

        setPrefSize(BELT_WIDTH + 40, 120);

        Rectangle beltRect = new Rectangle(BELT_WIDTH, BELT_HEIGHT);
        beltRect.setFill(Color.DARKGRAY);
        beltRect.setArcHeight(20);
        beltRect.setArcWidth(20);
        beltRect.setLayoutX(20);
        beltRect.setLayoutY(30);

        getChildren().add(beltRect);

        startAnimation();
    }

    private void startAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderPackages();
            }
        };
        timer.start();
    }

    private void renderPackages() {
        try {
            belt.getMutex().acquire();

            Deque<Float> positions = belt.getPackagePositions();

            // Anzahl der Nodes anpassen
            while (packageNodes.size() < positions.size()) {
                Rectangle pkg = new Rectangle(PACKAGE_SIZE, PACKAGE_SIZE);
                pkg.setFill(Color.CORNFLOWERBLUE);
                pkg.setArcWidth(6);
                pkg.setArcHeight(6);
                packageNodes.add(pkg);
                getChildren().add(pkg);
            }

            while (packageNodes.size() > positions.size()) {
                Rectangle r = packageNodes.remove(packageNodes.size() - 1);
                getChildren().remove(r);
            }

            int i = 0;
            for (Float pos : positions) {
                Rectangle pkg = packageNodes.get(i++);
                double x = PACKAGE_SIZE + (pos / 100.0) * (BELT_WIDTH - PACKAGE_SIZE);
                double y = 30 + (BELT_HEIGHT - PACKAGE_SIZE) / 2;

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
