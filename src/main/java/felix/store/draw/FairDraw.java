package felix.store.draw;

import felix.store.EnergyStore;
import felix.store.EnergyStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FairDraw implements DrawStrategy {
    private final EnergyStoreRepository energyStoreRepository;

    public Float draw(Float amount, Long networkId) {
        Map<String, Double> networkCapacity = energyStoreRepository.getCapacity(networkId);
        float networkCurrentCapacity = networkCapacity.get("currentCapacity").floatValue();

        if (amount > networkCurrentCapacity) {
            throw new DrawBelowZeroException(amount, networkId, networkCurrentCapacity);
        }

        List<EnergyStore> energyStores = energyStoreRepository.findByNetworkAscPercentage(networkId);

        float networkMaxCapacity = networkCapacity.get("maxCapacity").floatValue();
        float targetPercentage = (networkCurrentCapacity - amount) / networkMaxCapacity;

        for (EnergyStore energyStore : energyStores) {
            if (energyStore.getMaxCapacity() * targetPercentage > energyStore.getCurrentCapacity()) {
                networkMaxCapacity -= energyStore.getMaxCapacity();
                networkCurrentCapacity -= energyStore.getCurrentCapacity();
                targetPercentage = (networkCurrentCapacity - amount) / networkMaxCapacity;
            } else {
                float targetValue = targetPercentage * energyStore.getMaxCapacity();
                energyStore.drawCapacity(energyStore.getCurrentCapacity() - targetValue);
            }
        }

        energyStoreRepository.saveAll(energyStores);

        return networkCurrentCapacity - amount;
    }
}
