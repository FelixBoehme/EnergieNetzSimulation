package felix.network;

public class NetworkNotFoundException extends RuntimeException {
    public NetworkNotFoundException(Long networkId) {
        super("Couldn't find Network with ID: " + networkId);
    }
}
