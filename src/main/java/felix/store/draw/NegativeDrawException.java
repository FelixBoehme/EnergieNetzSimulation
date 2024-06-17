package felix.store.draw;

public class NegativeDrawException extends RuntimeException {
    public NegativeDrawException(Float amount, Long networkId) {
        super("Can't draw " + amount + " (negative capacity) from Network with ID " + networkId); // TODO: add unit to value
    }
}
