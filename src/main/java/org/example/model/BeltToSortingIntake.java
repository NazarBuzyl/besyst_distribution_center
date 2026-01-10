package org.example.model;

import org.example.model.conveyorBelt.ConveyorBelt;
import org.example.model.conveyorBelt.ConveyorBeltArray;

public class BeltToSortingIntake extends Thread {

    private final int beltIndex; // 1-based
    private final int employeeId;
    private final ConveyorBeltArray inBelts;
    private final SortingRoom sortingRoom;

    private volatile boolean running = true;

    public BeltToSortingIntake(int employeeId, int beltIndex, ConveyorBeltArray inBelts, SortingRoom sortingRoom) {
        this.employeeId = employeeId;
        this.beltIndex = beltIndex;
        this.inBelts = inBelts;
        this.sortingRoom = sortingRoom;

        if (beltIndex < 1 || beltIndex > inBelts.getBelts().size()) {
            throw new IllegalArgumentException("Invalid belt index: " + beltIndex);
        }
    }

    public void requestStop() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        ConveyorBelt inputBelts = inBelts.getBelts().get(beltIndex - 1);

        try {
            while (running && !isInterrupted()) {

                // Blockiert bis am Ende genau dieses Bandes ein Paket angekommen ist
                Package p = inBelts.pickPackageFrom(beltIndex, employeeId);

                // Blockiert, wenn SortingRoom voll ist
                sortingRoom.putForSorting(p);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

