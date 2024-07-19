package felix.network;

public class StoreNotInNetworkException extends RuntimeException {
    public StoreNotInNetworkException(Long networkId) {
        super("Can't delete store with ID: " + networkId + ", because it currently isn't assigned to a network");
    }
}
