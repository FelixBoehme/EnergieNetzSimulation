package felix.network;

public class DeleteStoreFromNetworkMismatch extends RuntimeException {
    public DeleteStoreFromNetworkMismatch(Long storeNetworkId, Long reqStoreId) {
        super("Won't delete because the network ID: " + storeNetworkId + " of the provided Store doesn't match the provided network ID: " + reqStoreId);
    }
}
