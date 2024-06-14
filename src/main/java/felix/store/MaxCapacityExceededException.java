package felix.store;

public class MaxCapacityExceededException extends RuntimeException {
    public MaxCapacityExceededException(Long storeId, Float amount) {
        super("Can't increase capacity of Store with ID " + storeId + " by " + amount + " because the result would exceed the Stores maximum capacity");
    }
}
