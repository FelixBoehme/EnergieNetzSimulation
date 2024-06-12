package felix.network;

import felix.store.EnergyStore;
import felix.store.EnergyStoreRepository;
import felix.store.draw.DrawStrategy;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NetworkService {

    @Autowired
    private EnergyStoreRepository energyStoreRepository;

    @Autowired
    private NetworkRepository networkRepository;

    @Autowired
    private Map<String, DrawStrategy> drawStrategies = new HashMap<>();

    Logger logger = LoggerFactory.getLogger(NetworkController.class);

    String networkNotFoundMessage = "Couldn't find Network with ID: ";

    private Network findNetwork(Long networkId) {
        return networkRepository.findById(networkId).orElseThrow(() -> {
            String error = networkNotFoundMessage + networkId;
            logger.error(error);
            return new EntityNotFoundException(error);
        });
    }

    public ResponseEntity<Network> addNetwork(Network network) {
        networkRepository.save(network);

        return new ResponseEntity<>(network, HttpStatus.CREATED);
    }

    public Network getNetwork(Long networkId) throws EntityNotFoundException {
        return findNetwork(networkId);
    }

    public ResponseEntity<EnergyStore> addEnergyStore(Long networkId, Long storeId) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(storeId).orElseThrow(() -> {
            String error = "Couldn't find Store with ID: " + storeId;
            logger.error(error);
            return new EntityNotFoundException(error);
        });
        Network network = findNetwork(networkId);
        energyStore.setNetwork(network);

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
            logger.error("Can't draw {} (negative capacity) from Network with ID {}", amount, networkId); // TODO: add unit to value
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        findNetwork(networkId);

        return drawStrategies.get(drawStrategy).draw(amount, networkId);
    }

    public Iterable<EnergyStore> getStores(Long networkId) {
        findNetwork(networkId);

        return energyStoreRepository.findByNetwork(networkId);
    }
}
