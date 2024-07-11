package felix.network;

public class DeleteStoreFromNetworkMismatchException extends RuntimeException {
    public DeleteStoreFromNetworkMismatchException(Long storeNetworkId, Long reqStoreId) {
        super("Won't delete because the network ID: " + storeNetworkId + " of the provided Store doesn't match the provided network ID: " + reqStoreId);
    }
}
