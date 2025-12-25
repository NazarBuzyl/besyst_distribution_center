
package org.example.model.employee;


import org.example.model.warehouse.Zone;
import org.example.model.conveyorBelt.ConveyorBelt;
import org.example.model.conveyorBelt.Package;

import java.util.Map;

public class Sorter extends Employee
{
    public static final long SORTING_SPEED = 500;

    private final ConveyorBelt pickTarget;

    private final Map<Zone, ConveyorBelt> dropTargets;

    public Sorter(int employeeId, ConveyorBelt pickTarget, Map<Zone, ConveyorBelt> dropTargets) {
        super(employeeId);
        this.pickTarget = pickTarget;
        this.dropTargets = dropTargets;
    }
    private Zone determineZone(int zipCode) {
        int idx = Math.abs(zipCode) % 5;

        switch (idx) {
            case 0:
                return Zone.NORTH;
            case 1:
                return Zone.SOUTH;
            case 2:
                return Zone.EAST;
            case 3:
                return Zone.WEST;
            default:
                return Zone.OUTSKIRTS;
        }
    }


    @Override
    public void run() {
        try {
            while (true) {
                Package p = pickTarget.pickPackage(getEmployeeId());

                long sortTime = determineSortTime(p.getZipCode());
                Thread.sleep(sortTime);

                Zone zone = determineZone(p.getZipCode());
                ConveyorBelt targetBelt = dropTargets.get(zone);

                if (targetBelt != null) {
                    targetBelt.dropPackage(getEmployeeId(), p);
                } else {
                    System.out.println("No target belt for zone " + zone);
                }

            }
        } catch (InterruptedException e) {
            System.err.println("Sorter Thread unterbrochen.");
            Thread.currentThread().interrupt();
        }
    }

    private long determineSortTime ( int zipCode){
        // Beispiel-Logik: Höhere PLZ-Zahlen (ferne Ziele) benötigen mehr Zeit für die Analyse.
        if (zipCode >= 90000) return 1000; // Komplexe/Ferne Ziele: 6 Sekunden
        if (zipCode >= 50000) return 500; // Mittlere Distanz: 3 Sekunden
        return SORTING_SPEED; // Standard: 1.5 Sekunden
    }
    private ConveyorBelt findTargetBelt ( int zipCode){
        // Beispiel: Nutzt die ersten zwei Ziffern der PLZ (z.B. 42xxx -> Key 4)
        int key = zipCode / 10000;

        // Versuche, das Zielband direkt über den Key abzurufen
        return dropTargets.get(key);
    }
}
