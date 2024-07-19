package felix.network;

import felix.store.*;
import felix.store.draw.DrawStrategy;
import felix.store.draw.NegativeDrawException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class NetworkService {
    private final EnergyStoreRepository energyStoreRepository;
    private final NetworkRepository networkRepository;

    @Autowired
    private final Map<String, DrawStrategy> drawStrategies = new HashMap<>();

    private Network findNetwork(Long networkId) {
        return networkRepository.findById(networkId).orElseThrow(() -> new NetworkNotFoundException(networkId));
    }

    public void addNetwork(Network network) {
        String networkName = network.getName();
        Boolean sameNameExists = networkRepository.existsByName(networkName);

        if (sameNameExists)
            throw new NetworkAlreadyExistsException(networkName);

        networkRepository.save(network);
    }

    public Network getNetwork(Long networkId) {
        return findNetwork(networkId);
    }

    public EnergyStore addEnergyStore(Long networkId, Long storeId) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));
        Network network = findNetwork(networkId);
        energyStore.setNetwork(network);

        networkRepository.increaseTotalStores(networkId);

        return energyStoreRepository.save(energyStore);
    }

    public EnergyStore deleteStoreFromNetwork(Long networkId, Long storeId) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));
        Network network = energyStore.getNetwork();

        if (network == null)
            throw new StoreNotInNetworkException(networkId);

        Long storeNetworkId = network.getId();

        if (!Objects.equals(networkId, storeNetworkId))
            throw new DeleteStoreFromNetworkMismatchException(storeNetworkId, networkId);

        energyStore.deleteFromNetwork();

        Float currentCapacity = energyStore.getCurrentCapacity();
        Float maxCapacity = energyStore.getMaxCapacity();
        networkRepository.updateCapacity(networkId, -currentCapacity, -maxCapacity);
        networkRepository.decreaseTotalStores(networkId);

        return energyStoreRepository.save(energyStore);
    }

    public Iterable<Network> getAllNetworks() {
        return networkRepository.findAll();
    }

    public Float drawCapacity(Long networkId, Float amount, String drawStrategy) {
        if (amount < 0) {
            throw new NegativeDrawException(amount, networkId);
        }

        findNetwork(networkId);

        return drawStrategies.get(drawStrategy).draw(amount, networkId);
    }

    public EnergyStoreListDTO getStores(Long networkId, Pageable pageable) {
        Long totalStores = findNetwork(networkId).getTotalStores();
        List<EnergyStoreDTO> stores = energyStoreRepository.findByNetwork(networkId, pageable).stream().map(EnergyStore::toDTO).toList();

        return new EnergyStoreListDTO(totalStores, stores);
    }
}
