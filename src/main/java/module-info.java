module org.example {
    requires javafx.controls;
    requires java.desktop;

    exports org.example;
    exports org.example.model;
    exports org.example.model.employee;
    exports org.example.model.conveyorBelt;
    exports org.example.model.transport;
    exports org.example.model.stations;
    exports org.example.model.stations.receiving;

    exports org.example.view;
    exports org.example.controller;
}
