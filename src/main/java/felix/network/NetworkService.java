package felix.network;

import felix.store.EnergyStore;
import felix.store.EnergyStoreNotFoundException;
import felix.store.EnergyStoreRepository;
import felix.store.draw.DrawStrategy;
import felix.store.draw.NegativeDrawException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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

    public ResponseEntity<Network> addNetwork(Network network) {
        networkRepository.save(network);

        return new ResponseEntity<>(network, HttpStatus.CREATED);
    }

    public Network getNetwork(Long networkId) {
        return findNetwork(networkId);
    }

    public ResponseEntity<EnergyStore> addEnergyStore(Long networkId, Long storeId) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));
        Network network = findNetwork(networkId);
        energyStore.setNetwork(network);

        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public ResponseEntity<EnergyStore> deleteStoreFromNetwork(Long networkId, Long storeId) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> new EnergyStoreNotFoundException(storeId));
        Long storeNetworkId = energyStore.getNetwork().getId();

        if (!Objects.equals(networkId, storeNetworkId))
            throw new DeleteStoreFromNetworkMismatch(storeNetworkId, networkId);

        energyStore.deleteFromNetwork();

        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public Iterable<Network> getAllNetworks() {
        return networkRepository.findAll();
    }

    public Map<String, Double> getCapacity(Long networkId) {
        findNetwork(networkId); // TODO: maybe only use one query and handle errors differently

        return energyStoreRepository.getCapacity(networkId);
    }

    public Float drawCapacity(Long networkId, Float amount, String drawStrategy) {
        if (amount < 0) {
            throw new NegativeDrawException(amount, networkId);
        }

        findNetwork(networkId);

        return drawStrategies.get(drawStrategy).draw(amount, networkId);
    }

    public Iterable<EnergyStore> getStores(Long networkId) {
        findNetwork(networkId);

        return energyStoreRepository.findByNetwork(networkId);
    }
}
