package felix.store.draw;

import felix.store.EnergyStore;
import felix.store.EnergyStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EvenDraw implements DrawStrategy {
    private final EnergyStoreRepository energyStoreRepository;

    public Float draw(Float amount, Long networkId) {
        Map<String, Double> networkCapacity = energyStoreRepository.getCapacity(networkId);
        float networkCurrentCapacity = networkCapacity.get("currentCapacity").floatValue();

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
        return networkCurrentCapacity - amount;
    }
}
