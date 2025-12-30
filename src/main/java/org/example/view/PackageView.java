package org.example.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PackageView extends Rectangle {
    public static final int PACKAGE_HEIGHT = 25;
    public static final int PACKAGE_WIDTH = 35;

    public PackageView() {
        super(PACKAGE_WIDTH, PACKAGE_HEIGHT);

        this.setFill(Color.BEIGE);
        this.setStroke(Color.BLACK);
    }
}
