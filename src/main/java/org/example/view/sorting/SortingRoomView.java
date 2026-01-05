package org.example.view.sorting;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.model.conveyorBelt.Package;
import org.example.model.sorting.SortingRoom;

import java.util.ArrayList;
import java.util.List;

public class SortingRoomView extends VBox {

    private static final double WIDTH = 300;
    private static final double HEIGHT = 600;
    private static final double PKG_SIZE = 20;

    private final SortingRoom sortingRoom;

    private final Label unsortedLabel = new Label();
    private final Label beingSortedLabel = new Label();

    private final Pane beingSortedPane = new Pane();
    private final Pane unsortedPane = new Pane();

    private final List<Rectangle> unsortedNodes = new ArrayList<>();
    private final List<Rectangle> beingSortedNodes = new ArrayList<>();

    public SortingRoomView(SortingRoom sortingRoom) {
        this.sortingRoom = sortingRoom;

        setPadding(new Insets(10));
        setSpacing(10);
        setPrefSize(WIDTH, HEIGHT);

        // рамка + background (schlicht)
        setBackground(new Background(new BackgroundFill(Color.web("#f6f6f6"), new CornerRadii(8), Insets.EMPTY)));
        setBorder(new Border(new BorderStroke(
                Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1)
        )));

        Label title = new Label("Sorting Room");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Being sorted Box (oben)
        VBox beingBox = new VBox(5);
        beingBox.setPadding(new Insets(8));
        beingBox.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(6), BorderWidths.DEFAULT)));
        beingSortedPane.setPrefHeight(60);
        beingSortedPane.setMinHeight(60);
        beingBox.getChildren().addAll(beingSortedLabel, beingSortedPane);

        // Unsorted Box (unten)
        VBox unsortedBox = new VBox(5);
        unsortedBox.setPadding(new Insets(8));
        unsortedBox.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(6), BorderWidths.DEFAULT)));
        VBox.setVgrow(unsortedPane, Priority.ALWAYS);
        unsortedPane.setPrefHeight(HEIGHT - 140);
        unsortedBox.getChildren().addAll(unsortedLabel, unsortedPane);

        getChildren().addAll(title, beingBox, unsortedBox);

        startAnimation();
    }

    private void startAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override public void handle(long now) {
                renderBeingSorted();
                renderUnsorted();
            }
        };
        timer.start();
    }

    private void renderUnsorted() {
        int size = sortingRoom.getUnsortedSize();
        unsortedLabel.setText("Unsorted (" + size + ")");

        // Nodes anpassen
        while (unsortedNodes.size() < size) {
            Rectangle r = new Rectangle(PKG_SIZE, PKG_SIZE);
            r.setFill(Color.LIGHTGRAY);
            r.setArcWidth(6);
            r.setArcHeight(6);
            unsortedNodes.add(r);
            unsortedPane.getChildren().add(r);
        }
        while (unsortedNodes.size() > size) {
            Rectangle r = unsortedNodes.remove(unsortedNodes.size() - 1);
            unsortedPane.getChildren().remove(r);
        }

        // Position (vertikaler Stapel)
        for (int i = 0; i < unsortedNodes.size(); i++) {
            Rectangle r = unsortedNodes.get(i);
            r.setLayoutX(10);
            r.setLayoutY(10 + i * (PKG_SIZE + 6));
        }
    }

    private void renderBeingSorted() {
        List<Package> being = sortingRoom.getBeingSorted();
        beingSortedLabel.setText("Being sorted (" + being.size() + ")");

        // Nodes anpassen (ohne alles löschen)
        while (beingSortedNodes.size() < being.size()) {
            Rectangle r = new Rectangle(PKG_SIZE, PKG_SIZE);
            r.setFill(Color.CRIMSON);
            r.setArcWidth(6);
            r.setArcHeight(6);
            beingSortedNodes.add(r);
            beingSortedPane.getChildren().add(r);
        }
        while (beingSortedNodes.size() > being.size()) {
            Rectangle r = beingSortedNodes.remove(beingSortedNodes.size() - 1);
            beingSortedPane.getChildren().remove(r);
        }

        // Position (horizontal oben)
        for (int i = 0; i < beingSortedNodes.size(); i++) {
            Rectangle r = beingSortedNodes.get(i);
            r.setLayoutX(10 + i * (PKG_SIZE + 6));
            r.setLayoutY(10);
        }
    }
}
