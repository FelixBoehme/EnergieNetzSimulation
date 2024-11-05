package felix.store.draw;

import felix.network.Network;
import felix.network.NetworkNotFoundException;
import felix.network.NetworkRepository;
import felix.store.EnergyStore;
import felix.store.EnergyStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FairDraw implements DrawStrategy {
    private final EnergyStoreRepository energyStoreRepository;
    private final NetworkRepository networkRepository;

    public Float draw(Float amount, Long networkId) {
        amount = Math.round(amount * 100.0f) / 100.0f;
        Network network = networkRepository.findById(networkId).orElseThrow(() -> new NetworkNotFoundException(networkId));
        float networkCurrentCapacity = network.getCurrentCapacity();

        if (amount > networkCurrentCapacity) {
            throw new DrawBelowZeroException(amount, networkId, networkCurrentCapacity);
        }

        List<EnergyStore> energyStores = energyStoreRepository.findByNetworkAscPercentage(networkId);

        float networkMaxCapacity = network.getMaxCapacity();
        float targetPercentage = (networkCurrentCapacity - amount) / networkMaxCapacity;

        for (EnergyStore energyStore : energyStores) {
            if (energyStore.getMaxCapacity() * targetPercentage > energyStore.getCurrentCapacity()) {
                networkMaxCapacity -= energyStore.getMaxCapacity();
                networkCurrentCapacity -= energyStore.getCurrentCapacity();
                targetPercentage = (networkCurrentCapacity - amount) / networkMaxCapacity;
            } else {
                float targetValue = Math.round(targetPercentage * energyStore.getMaxCapacity() * 100.0f) / 100.0f;
                energyStore.drawCapacity(energyStore.getCurrentCapacity() - targetValue);
            }
        }

        energyStoreRepository.saveAll(energyStores);
        networkRepository.updateCapacity(networkId, -amount, 0F);

        return networkCurrentCapacity - amount;
    }
}
