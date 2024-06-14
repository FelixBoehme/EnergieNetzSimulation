package felix.store;

public class EnergyStoreNotFoundException extends RuntimeException {
    public EnergyStoreNotFoundException(Long storeId) {
        super("Couldn't find Store with ID: " + storeId);
    }
}
