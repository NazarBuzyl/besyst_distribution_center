package org.example.view;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.example.model.transport.TransportInput;
import org.example.model.transport.TransportObserver;

import java.util.*;

/**
 * @author Nazar Buzyl
 */
public class SectionTransport extends BorderPane {
    private final List<TransportInput> trucks;
    private final List<TransportObserver> truckObservers;
    private final List<TruckView> truckViews = new LinkedList<>();;

    private final VBox trucksVBox;

    public SectionTransport(List<TransportInput> trucks, List<TransportObserver> truckObservers) {
        this.trucksVBox = new VBox(20);
        this.trucks = trucks;
        this.truckObservers = truckObservers;

        for(int i = 0; i < trucks.size(); i++)
        {
            addTransport(new AbstractMap.SimpleEntry<>(trucks.get(i), truckObservers.get(i)));
        }
        setMinWidth(330);

        getChildren().addAll(trucksVBox);
        setAlignment(trucksVBox, Pos.CENTER);
    }

    public void addTransport(Map.Entry<TransportInput, TransportObserver> entry) {
        TruckView trackView = new TruckView(entry.getKey(), entry.getValue());
        this.trucksVBox.getChildren().add(trackView);
        this.truckViews.add(trackView);
    }
}
