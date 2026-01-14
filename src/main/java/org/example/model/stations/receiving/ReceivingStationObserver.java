package org.example.model.stations.receiving;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * @author Nazar Buzyl
 */
public class ReceivingStationObserver {

    private final BooleanProperty receiving = new SimpleBooleanProperty(false);
    private final IntegerProperty packages = new SimpleIntegerProperty(0);
    private final IntegerProperty totalPackages = new SimpleIntegerProperty(0);
    private final IntegerProperty employees = new SimpleIntegerProperty(0);


    public BooleanProperty receivingProperty() {
        return receiving;
    }
    public IntegerProperty totalPackagesProperty() {return totalPackages;}

    public IntegerProperty packagesProperty() {
        return packages;
    }
    public IntegerProperty employeesProperty() {
        return employees;
    }

    public void changePackages(int newPackages) {
        packages.set(newPackages);
    }

    public void changeEmployees(int newEmployees) {
        employees.set(newEmployees);
    }

    public void startReceiving() {
        receiving.set(true);
    }

    public void finishReceiving() {
        receiving.set(false);
    }

    public void addToTotalPackage(int newPackages) {
        totalPackages.set(totalPackages.getValue() + newPackages);
    }
}
