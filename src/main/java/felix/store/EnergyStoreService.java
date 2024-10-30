package felix.store;

import felix.network.Network;
import felix.network.NetworkNotFoundException;
import felix.network.NetworkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EnergyStoreService {
    private final NetworkRepository networkRepository;
    private final EnergyStoreRepository energyStoreRepository;

    public EnergyStore getEnergyStore(Long storeId) {
        return energyStoreRepository.findById(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));
    }

    public EnergyStoreListDTO getActiveEnergyStores(Pageable pageable, Specification<EnergyStore> spec) {
        Page<EnergyStore> page = energyStoreRepository.findAll(spec, pageable);
        Long totalStores = page.getTotalElements();
        List<EnergyStoreDTO> stores = page.getContent().stream().map(EnergyStore::toDTO).toList();
        return new EnergyStoreListDTO(totalStores, stores);
    }

    public EnergyStore updateCurrentCapacity(Long storeId, Float change) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));

        if (change < 0) {
            throw new NegativeChangeException(storeId, change);
        }

        energyStore.increaseCapacity(change);

        long networkId = energyStore.getNetwork().getId();
        networkRepository.updateCapacity(networkId, change, 0F);

        return energyStoreRepository.save(energyStore);
    }

    public EnergyStore softDeleteEnergyStore(Long storeId) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));

        energyStore.setDeleted(true);

        if (energyStore.getNetwork() != null) {
            Long networkId = energyStore.getNetwork().getId();
            Float currentCapacity = energyStore.getCurrentCapacity();
            Float maxCapacity = energyStore.getMaxCapacity();
            networkRepository.updateCapacity(networkId, -currentCapacity, -maxCapacity);
            networkRepository.decreaseTotalStores(energyStore.getNetwork().getId());
        }

        return energyStoreRepository.save(energyStore);
    }

    public EnergyStore addEnergyStore(NewEnergyStore newEnergyStore) {
        EnergyStore energyStore = newEnergyStore.toEnergyStore();
        return energyStoreRepository.save(energyStore);
    }

    public EnergyStore addEnergyStoreWithNetwork(NewEnergyStore newEnergyStore, Long networkId) {
        Network network = networkRepository.findById(networkId).orElseThrow(() -> new NetworkNotFoundException(networkId));
        EnergyStore energyStore = newEnergyStore.toEnergyStore(network);
        energyStoreRepository.save(energyStore);

        Float currentCapacity = energyStore.getCurrentCapacity();
        Float maxCapacity = energyStore.getMaxCapacity();
        networkRepository.updateCapacity(networkId, currentCapacity, maxCapacity);
        networkRepository.increaseTotalStores(networkId);

        return energyStore;
    }

    public List<EnergyStoreDTO> getUnassignedEnergyStores() {
        return energyStoreRepository.findUnassigned().stream().map(EnergyStore::toDTO).toList();
    }

    public EnergyStore editEnergyStore(NewEnergyStore newEnergyStore, Long storeId) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));
        energyStore.setType(newEnergyStore.getType());
        energyStore.setLocation(newEnergyStore.getLocation());

        Float currentCapacityChange = newEnergyStore.getCurrentCapacity() - energyStore.getCurrentCapacity();
        Float maxCapacityChange = newEnergyStore.getMaxCapacity() - energyStore.getMaxCapacity();

        energyStore.setMaxCapacity(newEnergyStore.getMaxCapacity());
        energyStore.setCurrentCapacity(newEnergyStore.getCurrentCapacity());

        Long networkId = energyStore.getNetwork().getId();
        networkRepository.updateCapacity(networkId, currentCapacityChange, maxCapacityChange);


        return energyStoreRepository.save(energyStore);
    }
}
