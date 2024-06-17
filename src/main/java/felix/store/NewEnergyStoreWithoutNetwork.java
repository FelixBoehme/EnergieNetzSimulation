package felix.store;

import felix.network.NetworkRepository;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

public class NewEnergyStoreWithoutNetwork {
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

    public NewEnergyStoreWithoutNetwork(EnergyStoreType type, Float maxCapacity, Float currentCapacity, String location) {
        this.type = type;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.location = location;
    }

    public EnergyStore toEnergyStore() {
        return new EnergyStore(type, maxCapacity, currentCapacity, location, null);
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
