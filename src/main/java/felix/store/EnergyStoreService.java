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

        float newCapacity = energyStore.getCurrentCapacity() + change;

        if (newCapacity < 0) {
            logger.error("Can't reduce capacity of Store with ID {}, by {}, because the result would be negative", storeId, change);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (newCapacity > energyStore.getMaxCapacity()) {
            logger.error("Can't increase capacity of Store with ID {}. by {}, because the result would exceed the Stores maximum capacity", storeId, change);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            energyStore.setCurrentCapacity(newCapacity);
        }
        energyStoreRepository.save(energyStore);

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

    // TODO: check if values provided aren't none => introduce new types used for receiving data annotated with @NotNull and use @Valid
    public ResponseEntity<EnergyStore> addEnergyStore(EnergyStore energyStore) {
        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.CREATED);
    }

    public ResponseEntity<EnergyStore> addEnergyStoreWithNetwork(EnergyStore energyStore, Long networkId) {
        Network network = networkRepository.findById(networkId).orElseThrow(() -> {
            String error = networkNotFoundMessage + networkId;
            return new EntityNotFoundException(error);
        });
        energyStore.setNetwork(network);
        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public Iterable<EnergyStore> getUnassignedEnergyStores() {
        return energyStoreRepository.findUnassigned();
    }
}
