package felix.store.draw;

import felix.store.EnergyStore;
import felix.store.EnergyStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EvenDraw implements DrawStrategy {
    @Autowired
    EnergyStoreRepository energyStoreRepository;

    public Float draw(Float amount, Long networkId) {
        Map<String, Double> networkCapacity = energyStoreRepository.getCapacity(networkId);
        float networkCurrentCapacity = networkCapacity.get("currentCapacity").floatValue();

        if (amount > networkCurrentCapacity) {
            throw new DrawBelowZeroException(amount, networkId, networkCurrentCapacity);
        }

        List<EnergyStore> energyStores = energyStoreRepository.findByNetworkPositiveCapacity(networkId);

        float drawnCapacity = 0F;
        int nmbOfStores = energyStores.size();

        // TODO: faire anteilige behandlung der batterien (strategy pattern)
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

                energyStore.drawCapacity(drawPerStore); // TODO: change to drawing instead of setting capacity
                drawnCapacity += drawPerStore;
            }
        }
        return networkCurrentCapacity - amount;
    }
}
