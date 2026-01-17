package org.example.view.conveyorBelt;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.model.conveyorBelt.ConveyorBelt;
import org.example.model.conveyorBelt.ConveyorBeltArray;

import java.util.*;


/**
 * @author Finn Kramer
 *
 * Diese Klasse beschreibt das GUI-Modul für eine Fließbandreihe.
 */
public class ConveyorBeltView extends VBox
{
    // Fließbandbreite in Pixeln
    private static final double BELT_WIDTH = 500;

    // Fließbandhöhe in Pixeln
    private static final double BELT_HEIGHT = 60;

    // Paketgröße in Pixeln
    private static final double PACKAGE_SIZE = 20;

    // Paketabstand in Pixeln
    private static final double SPACING = 20;

    // Fließbandreihe
    private final ConveyorBeltArray conveyorBeltArray;

    // Zu jedem Band: seine Paket-Rechtecke
    private final Map<ConveyorBelt, List<Rectangle>> packageNodes = new HashMap<>();

    // Zu jedem Band: sein Pane
    private final Map<ConveyorBelt, Pane> beltPanes = new HashMap<>();


    /**
     * Initialisiere eine Instanz aus einer Fließbandreihe.
     *
     * @param conveyorBeltArray Fließbandreihe
     */
    public ConveyorBeltView(ConveyorBeltArray conveyorBeltArray) {
        this.conveyorBeltArray = conveyorBeltArray;

        setSpacing(SPACING);
        setPrefWidth(BELT_WIDTH + 40);

        initButtons();
        initBelts();
        startAnimation();
    }


    /**
     * Initialisiere Buttons zum Hinzufügen und Entfernen von Fließbändern.
     */
    private void initButtons() {
        Button removeBeltButton = new Button("Remove belt");
        Button addBeltButton = new Button("Add belt");

        removeBeltButton.setOnAction(event -> {
            ConveyorBelt removedBelt = this.conveyorBeltArray.removeBelt();
            if (removedBelt != null) {
                Pane beltPane = this.beltPanes.get(removedBelt);
                this.getChildren().remove(beltPane);
                this.beltPanes.remove(removedBelt);
                this.packageNodes.remove(removedBelt);
            }
        });

        addBeltButton.setOnAction(event -> {
            ConveyorBelt newBelt = this.conveyorBeltArray.addBelt();
            Pane beltPane = createBeltPane();
            this.getChildren().add(beltPane);
            this.beltPanes.put(newBelt, beltPane);
            this.packageNodes.put(newBelt, new ArrayList<>());
        });

        removeBeltButton.setPadding(new Insets(10));
        addBeltButton.setPadding(new Insets(10));
        this.setAlignment(Pos.TOP_CENTER);

        this.getChildren().add(addBeltButton);
        this.getChildren().add(removeBeltButton);
    }


    /**
     * Initialisiere alle Fließbänder.
     */
    private void initBelts() {
        for (ConveyorBelt belt : conveyorBeltArray.getBelts()) {
            Pane beltPane = createBeltPane();
            beltPanes.put(belt, beltPane);
            packageNodes.put(belt, new ArrayList<>());
            getChildren().add(beltPane);
        }
    }


    /**
     * Erstelle ein Pane für ein Fließband.
     *
     * @return Pane eines Fließbands
     */
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


    /**
     * Starte die Animation der Fließbänder.
     */
    private void startAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderAllBelts();
            }
        };
        timer.start();
    }


    /**
     * Rendere alle Fließbänder.
     */
    private void renderAllBelts()
    {
        // Erstelle eine Kopie der Keys, um ConcurrentModificationException zu vermeiden
        Set<ConveyorBelt> beltsToRender = new HashSet<>(beltPanes.keySet());

        for (ConveyorBelt belt : beltsToRender) {
            // Überprüfe, ob das Belt noch existiert (könnte zwischenzeitlich entfernt worden sein)
            if (beltPanes.containsKey(belt)) {
                renderBelt(belt);
            }
        }
    }


    /**
     * Rendere ein einzelnes Fließband.
     *
     * @param belt Fließband
     */
    private void renderBelt(ConveyorBelt belt)
    {
        Pane pane = beltPanes.get(belt);
        List<Rectangle> nodes = packageNodes.get(belt);

        if (pane == null || nodes == null) {
            return;
        }

        try {
            belt.getMutex().acquire();

            Deque<Float> positions = belt.getPackagePositions();

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