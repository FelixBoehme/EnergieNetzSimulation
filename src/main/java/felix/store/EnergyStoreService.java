package felix.store;

import felix.network.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class EnergyStoreService {
    private final NetworkRepository networkRepository;
    private final EnergyStoreRepository energyStoreRepository;

    Logger logger = LoggerFactory.getLogger(NetworkController.class);

    String storeNotFoundMessage = "Couldn't find Store with ID: ";
    String networkNotFoundMessage = "Couldn't find Network with ID: ";

    public EnergyStore getEnergyStore(Long storeId) {
        return energyStoreRepository.findById(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));
    }

    public Iterable<EnergyStore> getActiveEnergyStores() {
        return energyStoreRepository.findAllActive();
    }

    public ResponseEntity<EnergyStore> updateCurrentCapacity(Long storeId, Float change) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));

        if (change < 0) {
            throw new NegativeChangeException(storeId, change);
        }

        energyStore.increaseCapacity(change);
        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public ResponseEntity<EnergyStore> softDeleteEnergyStore(Long storeId) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));
        energyStore.setDeleted(true);

        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public ResponseEntity<EnergyStore> addEnergyStore(NewEnergyStoreWithoutNetwork newEnergyStore) {
        EnergyStore energyStore = newEnergyStore.toEnergyStore();
        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.CREATED);
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
