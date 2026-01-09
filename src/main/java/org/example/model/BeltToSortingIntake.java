package org.example.model;

import org.example.model.conveyorBelt.ConveyorBeltArray;
import org.example.model.Package;

public class BeltToSortingIntake extends Thread {

    private final int employeeId;
    private final int inputBeltIndex;
    private final ConveyorBeltArray inputBelts;
    private final SortingRoom sortingRoom;

    private volatile boolean running = true;

    public BeltToSortingIntake(int employeeId, int inputBeltIndex,
                               ConveyorBeltArray inputBelts,
                               SortingRoom sortingRoom) {
        this.employeeId = employeeId;
        this.inputBeltIndex = inputBeltIndex;
        this.inputBelts = inputBelts;
        this.sortingRoom = sortingRoom;
    }

    public void requestStop() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        try {
            while (running && !isInterrupted()) {
                // blockiert bis am Ende des Bandes ein Paket "abholbar" ist
                Package p = inputBelts.pickPackageFrom(inputBeltIndex, employeeId);

                // blockiert wenn SortingRoom voll ist
                sortingRoom.putForSorting(p);
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}

