package org.example.view;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.example.model.TransportInput;
import org.example.model.TransportObserver;

import java.util.*;

public class TransportSection extends BorderPane {
    private final List<TransportInput> trucks;
    private final List<TransportObserver> truckObservers;
    private final List<TruckView> truckViews = new LinkedList<>();;

    private final VBox trucksVBox;

    public TransportSection(List<TransportInput> trucks, List<TransportObserver> truckObservers) {
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