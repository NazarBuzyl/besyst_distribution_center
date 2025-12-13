package org.example.model.conveyorBelt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * @author Finn Kramer
 */
public class ConveyorBeltDriver extends Thread {

    public static final float SPEED = 10; // % per second
    public static final int FPS = 12;
    public static final int REFRESHING_RATE = 1000 / FPS;

    public static final float MIN_DISTANCE =
            100F / (ConveyorBelt.CAPACITY - 1);

    public static final float POSITION_CHANGE_RATE =
            (1F / FPS) * SPEED;

    private boolean entranceLocked = false;
    private boolean exitLocked = true;

    private final ConveyorBelt target;

    public ConveyorBeltDriver(ConveyorBelt target) {
        this.target = target;
    }

    @Override
    public void run() {
        while (true) {
            try {
                updateConveyorBelt();
                Thread.sleep(REFRESHING_RATE);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void updateConveyorBelt() throws InterruptedException {

        target.getMutex().acquire();

        try {
            Deque<Float> deque = target.getPackagePositions();
            List<Float> positions = new ArrayList<>(deque);

            for (int i = 0; i < positions.size(); i++) {
                float pos = positions.get(i);

                if (i < positions.size() - 1) {
                    float next = positions.get(i + 1);
                    if (next - pos < MIN_DISTANCE) {
                        continue;
                    }
                }

                if (pos < 100F) {
                    positions.set(i,
                            Math.min(pos + POSITION_CHANGE_RATE, 100F));
                }
            }

            deque.clear();
            deque.addAll(positions);

            updateLocks(positions);

            System.out.println(Arrays.toString(positions.toArray()));

        } finally {
            target.getMutex().release();
        }
    }

    private void updateLocks(List<Float> positions) {

        if (positions.isEmpty() || positions.get(0) >= MIN_DISTANCE) {
            unlockEntrance();
        } else {
            entranceLocked = true;
        }

        if (!positions.isEmpty()
                && positions.get(positions.size() - 1) >= 100F) {
            unlockExit();
        } else {
            exitLocked = true;
        }
    }

    private void unlockEntrance() {
        if (entranceLocked) {
            target.getSemaWrite().release();
        }
        entranceLocked = false;
    }

    private void unlockExit() {
        if (exitLocked) {
            target.getSemaRead().release();
        }
        exitLocked = false;
    }
}
