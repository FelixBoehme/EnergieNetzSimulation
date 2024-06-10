package felix.store;

import felix.network.Network;
import felix.network.NetworkController;
import felix.network.NetworkRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class EnergyStoreService {
    @Autowired
    private NetworkRepository networkRepository;

    @Autowired
    private EnergyStoreRepository energyStoreRepository;

    Logger logger = LoggerFactory.getLogger(NetworkController.class);

    String storeNotFoundMessage = "Couldn't find Store with ID: ";
    String networkNotFoundMessage = "Couldn't find Network with ID: ";

    public EnergyStoreService(NetworkRepository networkRepository, EnergyStoreRepository energyStoreRepository) {
        this.networkRepository = networkRepository;
        this.energyStoreRepository = energyStoreRepository;
    }

    public EnergyStore getEnergyStore(Long storeId) {
        return energyStoreRepository.findById(storeId).orElseThrow(() -> {
            String error = storeNotFoundMessage + storeId;
            return new EntityNotFoundException(error);
        });
    }

    public ResponseEntity<EnergyStore> deleteStoreFromNetwork(Long storeId) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> {
            String error = storeNotFoundMessage + storeId;
            return new EntityNotFoundException(error);
        });
        energyStore.deleteFromNetwork();

        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public Iterable<EnergyStore> getActiveEnergyStores() {
        return energyStoreRepository.findAllActive();
    }

    public ResponseEntity<EnergyStore> updateCurrentCapacity(Long storeId, Float change) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> {
            String error = storeNotFoundMessage + storeId;
            return new EntityNotFoundException(error);
        });

        if (change <= 0) {
            logger.error("Can't increase capacity of Store with ID {}, by {}, because it isn't a positive number", storeId, change);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        try {
            energyStore.increaseCapacity(change);
            energyStoreRepository.save(energyStore);
        } catch (ResponseStatusException e) { // TODO: catch concrete error and use its error message
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public ResponseEntity<EnergyStore> softDeleteEnergyStore(Long storeId) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> {
            String error = storeNotFoundMessage + storeId;
            return new EntityNotFoundException(error);
        });
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
        Network network = networkRepository.findById(networkId).orElseThrow(() -> {
            String error = networkNotFoundMessage + networkId;
            return new EntityNotFoundException(error);
        });

        EnergyStore energyStore = newEnergyStore.toEnergyStore(network);

        energyStore.setNetwork(network);
        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public Iterable<EnergyStore> getUnassignedEnergyStores() {
        return energyStoreRepository.findUnassigned();
    }
}
