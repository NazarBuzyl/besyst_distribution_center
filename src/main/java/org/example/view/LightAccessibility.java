package org.example.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class LightAccessibility extends Circle {
    private boolean accessibility;

    public LightAccessibility(double size, boolean startState) {
        super(size);
        setAccessibility(startState);
        this.setStroke(Color.BLACK);
    }

    public LightAccessibility(double size) {
        super(size);
        setAccessibility(true);
        this.setStroke(Color.BLACK);
    }

    public void changeLight() {
        if (this.accessibility) {
            this.setFill(Color.RED);
        } else  {
            this.setFill(Color.LIMEGREEN);
        }
    }

    public void setAccessibility(boolean accessibility) {
        this.accessibility = accessibility;
        if (this.accessibility) {
            this.setFill(Color.LIMEGREEN);
        } else {
            this.setFill(Color.RED);
        }
    }
}
