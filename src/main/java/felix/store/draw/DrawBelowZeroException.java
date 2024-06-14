package felix.store.draw;

public class DrawBelowZeroException extends RuntimeException {
    public DrawBelowZeroException(Float amount, Long networkId, Float networkCurrentCapacity) {
        super("Can't draw "  + amount + " from Network with ID " + networkId + " because it has a maximum capacity of " + networkCurrentCapacity); // TODO: add unit to value
    }
}
