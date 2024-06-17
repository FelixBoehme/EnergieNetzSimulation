package felix.store;

import felix.network.Network;
import felix.network.NetworkNotFoundException;
import felix.network.NetworkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnergyStoreService {
    private final NetworkRepository networkRepository;
    private final EnergyStoreRepository energyStoreRepository;

    public EnergyStore getEnergyStore(Long storeId) {
        return energyStoreRepository.findById(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));
    }

    public Iterable<EnergyStore> getActiveEnergyStores() {
        return energyStoreRepository.findAllActive();
    }

    public EnergyStore updateCurrentCapacity(Long storeId, Float change) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));

        if (change < 0) {
            throw new NegativeChangeException(storeId, change);
        }

        energyStore.increaseCapacity(change);
        return energyStoreRepository.save(energyStore);
    }

    public EnergyStore softDeleteEnergyStore(Long storeId) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));
        energyStore.setDeleted(true);

        return energyStoreRepository.save(energyStore);
    }

    public EnergyStore addEnergyStore(NewEnergyStoreWithoutNetwork newEnergyStore) {
        EnergyStore energyStore = newEnergyStore.toEnergyStore();
        return energyStoreRepository.save(energyStore);
    }

    public ResponseEntity<EnergyStore> addEnergyStoreWithNetwork(NewEnergyStore newEnergyStore, Long networkId) {
        Network network = networkRepository.findById(networkId).orElseThrow(() -> new NetworkNotFoundException(networkId));

        EnergyStore energyStore = newEnergyStore.toEnergyStore(network);

        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public Iterable<EnergyStore> getUnassignedEnergyStores() {
        return energyStoreRepository.findUnassigned();
    }
}
