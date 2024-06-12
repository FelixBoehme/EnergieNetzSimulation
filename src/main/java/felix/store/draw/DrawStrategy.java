package felix.store.draw;

import felix.store.EnergyStore;

import java.util.List;

public interface DrawStrategy {
    Float draw(Float amount, Long networkId);
}
