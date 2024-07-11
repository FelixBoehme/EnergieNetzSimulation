package felix.network;

public class NetworkAlreadyExistsException extends RuntimeException {
    public NetworkAlreadyExistsException(String networkName) {
        super("A network with the name \"" + networkName + "\" already exists!");
    }
}
