import model.conveyorBelt.ConveyorBelt;
import model.conveyorBelt.ConveyorBeltDriver;
import model.employee.Dropper;
import model.employee.Sorter;
public class Main
{
    public static void main(String[] args)
    {
        ConveyorBelt cb = new ConveyorBelt(1);

        Dropper dropper = new Dropper(1, cb);

        Sorter sorter = new Sorter(2, cb);

        ConveyorBeltDriver cbd = new ConveyorBeltDriver(cb);

        cbd.start();
        dropper.start();
        sorter.start();

        try
        {
            cbd.join();
            dropper.join();
            sorter.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
