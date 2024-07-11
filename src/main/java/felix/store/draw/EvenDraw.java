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
public class EvenDraw implements DrawStrategy {
    private final EnergyStoreRepository energyStoreRepository;
    private final NetworkRepository networkRepository;

    public Float draw(Float amount, Long networkId) {
        Network network = networkRepository.findById(networkId).orElseThrow(() -> new NetworkNotFoundException(networkId));
        float networkCurrentCapacity = network.getCurrentCapacity();

        if (amount > networkCurrentCapacity) {
            throw new DrawBelowZeroException(amount, networkId, networkCurrentCapacity);
        }

        List<EnergyStore> energyStores = energyStoreRepository.findByNetworkPositiveCapacity(networkId);

        float drawnCapacity = 0F;
        int nmbOfStores = energyStores.size();

        while (drawnCapacity < amount) {
            float drawPerStore = (amount - drawnCapacity) / nmbOfStores;

            for (EnergyStore energyStore : energyStores) {
                Float storeCapacity = energyStore.getCurrentCapacity();
                if (storeCapacity == 0) continue;

                // set the draw per iteration to the highest possible value
                if (drawPerStore > storeCapacity) {
                    drawPerStore = storeCapacity;
                    nmbOfStores -= 1; // store is now empty, so it can't be drawn from
                }

                energyStore.drawCapacity(drawPerStore);
                drawnCapacity += drawPerStore;
            }
        }

        networkRepository.updateCapacity(networkId, -amount, 0F);

        return networkCurrentCapacity - amount;
    }
}
