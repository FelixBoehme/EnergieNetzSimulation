package felix.store;

import felix.network.Network;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
public class NewEnergyStore {
    @NotNull
    private final EnergyStoreType type;
    @NotNull
    private final Float maxCapacity;
    @NotNull
    private final Float currentCapacity;
    @NotNull
    private final String location;
    String networkNotFoundMessage = "Couldn't find Network with ID: ";

    public NewEnergyStore(EnergyStoreType type, Float maxCapacity, Float currentCapacity, String location) {
        this.type = type;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.location = location;
    }

    //TODO: include message in http response
    @AssertTrue(message = "The current capacity can't be greater than the maximum capacity")
    private boolean isCapacityValid() {
        if (maxCapacity == null || currentCapacity == null) return false;
        return maxCapacity >= currentCapacity;
    }

    public EnergyStore toEnergyStore() {
        return new EnergyStore(type, maxCapacity, currentCapacity, location, null);
    }

    public EnergyStore toEnergyStore(Network network) {
        return new EnergyStore(type, maxCapacity, currentCapacity, location, network);
    }
}
