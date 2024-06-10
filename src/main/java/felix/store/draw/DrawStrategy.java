package felix.store.draw;

import felix.store.EnergyStore;

import java.util.List;

public interface DrawStrategy {
    void draw(List<EnergyStore> energyStores, Float amount, Float networkCurrentCapacity, Float networkMaxCapacity);
}
