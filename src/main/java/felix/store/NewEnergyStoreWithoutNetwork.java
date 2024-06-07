package felix.store;

import felix.network.Network;
import felix.network.NetworkRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

public class NewEnergyStoreWithoutNetwork {
    @Autowired
    private NetworkRepository networkRepository;

    String networkNotFoundMessage = "Couldn't find Network with ID: ";

    @NotNull
    private final EnergyStoreType type;

    @NotNull
    private final Float maxCapacity;

    @NotNull
    private final Float currentCapacity;

    @NotNull
    private final String location;

    public NewEnergyStoreWithoutNetwork(EnergyStoreType type, Float maxCapacity, Float currentCapacity, String location) {
        this.type = type;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.location = location;
    }

    public EnergyStore toEnergyStore() {
        return new EnergyStore(type, maxCapacity, currentCapacity, location, null);
    }

    public NetworkRepository getNetworkRepository() {
        return networkRepository;
    }

    public String getNetworkNotFoundMessage() {
        return networkNotFoundMessage;
    }

    public EnergyStoreType getType() {
        return type;
    }

    public Float getMaxCapacity() {
        return maxCapacity;
    }

    public Float getCurrentCapacity() {
        return currentCapacity;
    }

    public String getLocation() {
        return location;
    }
}