
package org.example.model.warehouse;

import java.util.concurrent.ThreadLocalRandom;

public class Zone {

    // ---------- NEU: Name für Ausgabe/Dispatcher ----------
    private final String name;

    public Zone(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    // ---------- NEU: Zonen passend zu Sorter-Mapping (Band 1..5) ----------
    public static final Zone OUT_1 = new Zone("OUT_BAND_1 (28717+)");
    public static final Zone OUT_2 = new Zone("OUT_BAND_2 (28307+)");
    public static final Zone OUT_3 = new Zone("OUT_BAND_3 (28237+)");
    public static final Zone OUT_4 = new Zone("OUT_BAND_4 (Rest)");

    // ---------- ALT (bleibt): PLZ Generator ----------
    public static volatile int[] VALID_PLZ = {
            28195, 28197, 28199,
            28201, 28203, 28205, 28207, 28209,
            28211, 28213, 28215,
            28217, 28219, 28237, 28239,
            28259, 28277, 28279,
            28307, 28309, 28325, 28327, 28329,
            28355, 28357, 28359,
            28717, 28719, 28755, 28757, 28759, 28777, 28779
    };

    /**
     * Generiert eine PLZ (zufällig). Die Zuordnung zu Bändern/Zonen erfolgt später durch den Sorter.
     */
    public static int randomPlz() {
        int index = (ThreadLocalRandom.current().nextInt(0,33));
        return VALID_PLZ[index];
    }
}