package org.example.model.employee;

import org.example.model.conveyorBelt.ConveyorBeltArray;
<<<<<<< Updated upstream
import org.example.model.conveyorBelt.Package;
import org.example.model.sorting.SortingRoom;
=======
import org.example.model.Package;
import org.example.model.SortingRoom;
>>>>>>> Stashed changes

public class Sorter extends Employee {

    public static final int SORTING_SPEED = 1000;

    private final ConveyorBeltArray beltArray;
    private final SortingRoom sortingRoom;

    private volatile boolean running = true;

    public Sorter(int employeeId, ConveyorBeltArray beltArray, SortingRoom sortingRoom) {
        super(employeeId);
        this.beltArray = beltArray;
        this.sortingRoom = sortingRoom;
    }

    public void requestStop() {
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        try {
            while (running && !Thread.currentThread().isInterrupted()) {

                Package p = sortingRoom.takeForSorting();

                sortingRoom.addBeingSorted(p);

                try {
                    System.out.println("Sorter " + getEmployeeId()
                            + " is sorting package PLZ=" + p.getZipCode());

                    Thread.sleep(SORTING_SPEED);

                    int targetBandIndex = determineTargetBeltIndex(p.getZipCode());

                    beltArray.dropPackageTo(targetBandIndex, getEmployeeId(), p);

                } finally {
                    sortingRoom.removeBeingSorted(p);
                }

                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int determineTargetBeltIndex(int zip) {

        if (zip < 28195 || zip > 28779) return 5; // 5 = "INVALID/OUTSIDE" (zur Seite legen)

        if (zip >= 28717) return 1; // eher Bremen-Nord (viele 287xx)
        if (zip >= 28307) return 2; // eher Osten/SE (viele 283xx)
        if (zip >= 28237) return 3; // eher Westen (vieles 2823x/28239)
        return 4;                   // Rest = Mitte/Süd (z.B. 281xx–2822x)
    }
}

