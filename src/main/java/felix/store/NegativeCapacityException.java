package felix.store;

public class NegativeCapacityException extends RuntimeException {
    public NegativeCapacityException(Long storeId, Float amount) {
        super("Can't draw " + amount + "from Store with ID " + storeId + "because the result would be negative");
    }
}
