package org.example.model.warehouse;

public class Zone {

    // einfache feste Liste
    public static final int[] VALID_PLZ = {
            28195, 28197, 28199,
            28201, 28203, 28205, 28207, 28209,
            28211, 28213, 28215,
            28217, 28219, 28237, 28239,
            28259, 28277, 28279,
            28307, 28309, 28325, 28327, 28329,
            28355, 28357, 28359,
            28717, 28719, 28755, 28757, 28759, 28777, 28779
    };

    // zufällige gültige PLZ zurückgeben
    public static int randomPlz() {
        int index = (int) (Math.random() * VALID_PLZ.length);
        return VALID_PLZ[index];
    }
}

