package felix.store;

import felix.network.Network;
import felix.network.NetworkRepository;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

public class NewEnergyStore {
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

    @AssertTrue(message = "The current capacity can't be greater than the maximum capacity")
    private boolean isCapacityValid() {
        if (maxCapacity == null || currentCapacity == null) return false;
        return maxCapacity >= currentCapacity;
    }

    public NewEnergyStore(EnergyStoreType type, Float maxCapacity, Float currentCapacity, String location) {
        this.type = type;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.location = location;
    }

    public EnergyStore toEnergyStore(Network network) {
        return new EnergyStore(type, maxCapacity, currentCapacity, location, network);
    }
}
