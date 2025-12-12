package model;

public class StationEmptyException extends RuntimeException {
    public StationEmptyException() {
        super("Station Belt is empty, waiting...");
    }
}
