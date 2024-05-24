package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class EnergyStoreService {
    @Autowired
    private NetworkRepository networkRepository;

    @Autowired
    private EnergyStoreRepository energyStoreRepository;

    public EnergyStoreService(NetworkRepository networkRepository, EnergyStoreRepository energyStoreRepository) {
        this.networkRepository = networkRepository;
        this.energyStoreRepository = energyStoreRepository;
    }

    public EnergyStore getenergyStore(Long id) {
        return energyStoreRepository.findById(id).orElseThrow(() -> new RuntimeException("not found"));
    }

    public ResponseEntity<EnergyStore> deleteEnergyStore(Long energyStoreId) {
        EnergyStore energyStore = energyStoreRepository.findById(energyStoreId).orElseThrow(() -> new RuntimeException("not found"));
        energyStore.deleteFromNetwork();

        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public Iterable<EnergyStore> getActiveEnergyStores() {
        return energyStoreRepository.findAllActive();
    }

    public ResponseEntity<EnergyStore> updateCurrentCapacity(Long energyStoreId, Float change) {
        EnergyStore energyStore = energyStoreRepository.findById(energyStoreId).orElseThrow(() -> new RuntimeException("not found"));

        float newCapacity = energyStore.getCurrentCapacity() + change;

        if (newCapacity < 0) {
            throw new RuntimeException("can't reduce capacity below zero");
        } else if (newCapacity > energyStore.getMaxCapacity()) {
            throw new RuntimeException("can't increase capacity beyond the maximum capacity");
        }
        else {
            energyStore.setCurrentCapacity(newCapacity);
        }
        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public ResponseEntity<EnergyStore> softDeleteEnergyStore(Long energyStoreId) {
        EnergyStore energyStore = energyStoreRepository.findById(energyStoreId).orElseThrow(() -> new RuntimeException("not found"));
        energyStore.setDeleted(true);

        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public ResponseEntity<EnergyStore> addEnergyStore(EnergyStore energyStore) {
        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.CREATED);
    }

    public ResponseEntity<EnergyStore> addEnergyStoreWithNetwork(EnergyStore energyStore, Long networkId) {
        Network network = networkRepository.findById(networkId).orElseThrow(() -> new RuntimeException("not found"));
        energyStore.setNetwork(network);
        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }
}
