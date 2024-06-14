package felix.store;

public class NegativeChangeException extends RuntimeException{
    public NegativeChangeException(Long storeId, Float amount) {
        super("Can't increase capacity of Store with ID " + storeId + " by " + amount + " because it isn't a positive number");
    }
}
