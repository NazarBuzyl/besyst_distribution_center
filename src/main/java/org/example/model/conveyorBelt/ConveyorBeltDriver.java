package org.example.model.conveyorBelt;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ConveyorBeltDriver extends Thread {

    public static final float SPEED = 10;
    public static final int FPS = 12;
    public static final int REFRESHING_RATE = 1000 / FPS;

    public static final float MIN_DISTANCE = 100F / (ConveyorBelt.CAPACITY - 1);

    public static final float POSITION_CHANGE_RATE = (1F / FPS) * SPEED;

    private boolean entranceLocked = false;
    private boolean exitLocked = true;

    private final ConveyorBelt target;
    private final BlockingQueue<ConveyorBelt> writableQueue;
    private final BlockingQueue<ConveyorBelt> readableQueue;

    public ConveyorBeltDriver(ConveyorBelt target,
                              BlockingQueue<ConveyorBelt> writableQueue,
                              BlockingQueue<ConveyorBelt> readableQueue) {
        this.target = target;
        this.writableQueue = writableQueue;
        this.readableQueue = readableQueue;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                updateConveyorBelt();
                Thread.sleep(REFRESHING_RATE);
            } catch (InterruptedException e) {
                interrupt();
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
                    if (next - pos < MIN_DISTANCE) continue;
                }

                if (pos < 100F) {
                    positions.set(i,
                            Math.min(pos + POSITION_CHANGE_RATE, 100F));
                }
            }

            deque.clear();
            deque.addAll(positions);

            updateLocks(positions);

        } finally {
            target.getMutex().release();
        }
    }

    private void updateLocks(List<Float> positions)
    {
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

    private void unlockEntrance()
    {
        if (entranceLocked) {
            target.getSemaWrite().release();
            this.writableQueue.offer(this.target);
        }
        entranceLocked = false;
    }

    private void unlockExit() {
        if (exitLocked) {
            target.getSemaRead().release();
            this.readableQueue.offer(this.target);
        }
        exitLocked = false;
    }
}
