package model.conveyorBelt;


import java.util.Arrays;
import java.util.List;
public class ConveyorBeltDriver extends Thread
{
    public static final float SPEED = 10; // Speed in (% of conveyor belt length) / s

    public static final int FPS = 12; // Animation frames per second

    public static final int REFRESHING_RATE = 1000 / FPS; // Animation update interval in ms

    public static final float MIN_DISTANCE = 100F / (ConveyorBelt.CAPACITY - 1); // Max distance between packages in % of conveyor belt length

    public static final float POSITION_CHANGE_RATE = (1F / FPS) * SPEED; // Change rate of the position per frame

    private boolean entranceLocked = false;

    private boolean exitLocked = true;

    private final ConveyorBelt target;

    public ConveyorBeltDriver(ConveyorBelt target)
    {
        this.target = target;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                updateConveyorBelt();
                Thread.sleep(REFRESHING_RATE);
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateConveyorBelt() throws InterruptedException
    {
        this.target.getMutex().acquire(); // Reserviere das Fließband.

        List<Float> positions = this.target.getPackagePositions(); // Hole die Paketpositionen.

        for (int i = 0; i < positions.size(); i++)
        {
            float pos = positions.get(i);

            if (i < positions.size() - 1) // Wenn dies nicht das letzte Paket ist
            {
                float next = positions.get(i + 1);

                if (next - pos < MIN_DISTANCE) // Prüfe den Abstand zwischen diesem und dem nachfolgenden Paket.
                {
                    continue; // Wenn der Abstand zu gering ist, darf die Position nicht verändert werden.
                }
            }

            if (pos < 100) // Pakete dürfen bis maximal 100 % bewegt werden
            {
                positions.set(i, Math.min(pos + POSITION_CHANGE_RATE, 100)); // Liefert den kleineren der beiden Werte.
            }
        }

        updateLocks(positions);

        this.target.getMutex().release();

        System.out.println(Arrays.toString(this.target.getPackagePositions().toArray()));
    }

    private void updateLocks(List<Float> positions)
    {
        if (positions.isEmpty() || positions.get(0) >= MIN_DISTANCE) // Wenn keine Pakete auf dem Fließband liegen und das erste Paket weit genug entfernt ist
        {
            unlockEntrance(); // Entriegle den Eingang.
        }
        else
        {
            entranceLocked = true; // Sonst, verriegle den Eingang.
        }

        if (!positions.isEmpty() && (positions.get(positions.size() - 1) >= 100F)) // Wenn Pakete auf dem Fließband liegen und das letzte Paket am Ende ankommt
        {
            unlockExit(); // Entriegle den Ausgang.
        }
        else
        {
            exitLocked = true; // Sonst, verriegle den Ausgang.
        }
    }

    private void unlockEntrance()
    {
        if (this.entranceLocked) // Wenn der Eingang verriegelt ist
        {
            this.target.getSemaWrite().release(); // Gib die Semaphore nur frei, wenn das Schloss vorher verriegelt war.
        }
        this.entranceLocked = false; // Entriegle den Eingang.
    }

    private void unlockExit()
    {
        if (this.exitLocked) // Wenn der Ausgang verriegelt ist
        {
            this.target.getSemaRead().release(); // Gib die Semaphore nur frei, wenn der Ausgang vorher verriegelt war.
        }
        this.exitLocked = false;
    }
}
