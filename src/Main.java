import model.ReceivingStation;
import model.TransportInput;

import java.util.LinkedList;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        ReceivingStation receivingStation = new ReceivingStation();

        List<TransportInput> transporters = new LinkedList<>();
        for(int i=1; i<=4; i++) {
            transporters.add(new TransportInput(i, receivingStation));
            transporters.getLast().start();
        }

//        List<Sorter> sorters = new LinkedList<>();
//        for(int i=1; i<=2; i++) {
//            sorters.add(new Sorter(i, receivingStation));
//            sorters.getLast().start();
//        }

//        InputTransporter transporter1 = new InputTransporter(1, receivingStation, 50000, 50);
//        transporter1.start();
//        InputTransporter transporter2 = new InputTransporter(2, receivingStation, 10000, 100);
//        transporter2.start();
//        InputTransporter transporter3= new InputTransporter(3, receivingStation);
//        transporter3.start();
//        InputTransporter transporter4 = new InputTransporter(4, receivingStation);
//        transporter4.start();
//        ConveyorBelt cb = new ConveyorBelt(1);
//
//        Dropper dropper = new Dropper(1, cb);
//
//        Sorter sorter = new Sorter(2, cb);
//
//        ConveyorBeltDriver cbd = new ConveyorBeltDriver(cb);
//
//        cbd.start();
//        dropper.start();
//        sorter.start();
//
//        try
//        {
//            cbd.join();
//            dropper.join();
//            sorter.join();
//        } catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }
    }
}
