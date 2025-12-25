package org.example.model.conveyorBelt;

public class Package {
    private final int zipCode;
    private float position = 0.0F;

    public Package(int zipCode) {
        this.zipCode = zipCode;
        this.position = 0.0F;
    }

    public int getZipCode() {
        return zipCode;
    }
    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Package [PLZ=" + zipCode + ", position=" + position + "]";
    }
}