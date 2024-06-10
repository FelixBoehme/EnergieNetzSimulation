package felix.store.draw;

import felix.store.EnergyStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EvenDraw implements DrawStrategy {
    public void draw(List<EnergyStore> energyStores, Float amount, Float networkCurrentCapacity, Float networkMaxCapacity) {
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

    }
}
