package felix.store.draw;

import felix.store.EnergyStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FairDraw implements DrawStrategy {
    public void draw(List<EnergyStore> energyStores, Float amount, Float networkCurrentCapacity, Float networkMaxCapacity) {
        float targetPercentage = (networkCurrentCapacity - amount) / networkMaxCapacity;

        for (EnergyStore energyStore: energyStores) {
            if (energyStore.getMaxCapacity() * targetPercentage > energyStore.getCurrentCapacity()) {
                networkMaxCapacity -= energyStore.getMaxCapacity();
                networkCurrentCapacity -= energyStore.getCurrentCapacity();
                targetPercentage = (networkCurrentCapacity - amount) / networkMaxCapacity;
            } else {
                float targetValue = targetPercentage * energyStore.getMaxCapacity();
                energyStore.drawCapacity(energyStore.getCurrentCapacity() - targetValue);
            }
        }
    }
}
