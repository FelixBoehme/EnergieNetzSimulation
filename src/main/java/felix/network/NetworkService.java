package felix.network;

import felix.store.EnergyStore;
import felix.store.EnergyStoreRepository;
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

    Logger logger = LoggerFactory.getLogger(NetworkController.class);

    String networkNotFoundMessage = "Couldn't find Network with ID: ";

    public ResponseEntity<Network> addNetwork(Network network) {
        networkRepository.save(network);

        return new ResponseEntity<>(network, HttpStatus.CREATED);
    }

    public Network getNetwork(Long networkId) throws EntityNotFoundException {
        return networkRepository.findById(networkId).orElseThrow(() -> {
            String error = networkNotFoundMessage + networkId;
            logger.error(error);
            return new EntityNotFoundException(error);
        });
    }

    public ResponseEntity<EnergyStore> addEnergyStore(Long networkId, Long energyStoreId) {
        EnergyStore energyStore = energyStoreRepository.findByIdActive(energyStoreId).orElseThrow(() -> {
            String error = "Couldn't find Store with ID: " + energyStoreId;
            logger.error(error);
            return new EntityNotFoundException(error);
        });
        Network network = networkRepository.findById(networkId).orElseThrow(() -> {
            String error = networkNotFoundMessage + energyStoreId;
            logger.error(error);
            return new EntityNotFoundException(error);
        });
        energyStore.setNetwork(network);

        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public Iterable<Network> getAllNetworks() {
        return networkRepository.findAll();
    }

    public Map<String, Float> getCapacity(Long networkId) {
        Network network = networkRepository.findById(networkId).orElseThrow(() -> {
            String error = networkNotFoundMessage + networkId;
            logger.error(error);
            return new EntityNotFoundException(error);
        }); // TODO: not needed anymore, remove and handle error in different place

        Iterable<EnergyStore> energyStores = energyStoreRepository.findByNetwork(networkId);

        Float maxCapacitySum = 0F;
        float currentCapacitySum = 0F;
        float percentageCapactity = 0F;

        for (EnergyStore energyStore : energyStores) {
            maxCapacitySum += energyStore.getMaxCapacity();
            currentCapacitySum += energyStore.getCurrentCapacity();
        }

        if (maxCapacitySum != 0F) {
            percentageCapactity = currentCapacitySum / maxCapacitySum;
        }

        HashMap<String, Float> map = new HashMap<>();
        map.put("maxCapacity", maxCapacitySum);
        map.put("currentCapacity", currentCapacitySum);
        map.put("percentageCapacity", percentageCapactity);

        return map;
    }

    public Float drawCapacity(Long networkId, Float amount) {
        if (amount < 0) {
            logger.error("Can't draw {} (negative capacity) from Network with ID {}", amount, networkId); // TODO: add unit to value
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Network network = networkRepository.findById(networkId).orElseThrow(() -> {
            String error = networkNotFoundMessage + networkId;
            logger.error(error);
            return new EntityNotFoundException(error);
        });

        List<EnergyStore> energyStores = energyStoreRepository.findByNetworkPositiveCapacity(networkId); // TODO: handle not finding the network

        float drawnCapacity = 0F;
        Float networkCapacity = getCapacity(networkId).get("currentCapacity");

        if (amount > networkCapacity) {
            logger.error("Can't draw {} from Network with ID {} because it has a maximum capacity of {}", amount, networkId, networkCapacity); // TODO: add units to values
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        int nmbOfStores = energyStores.size();

        while (drawnCapacity < amount) {
            float drawPerStore = (amount - drawnCapacity) / nmbOfStores;

            for (EnergyStore energyStore : energyStores) {
                Float storeCapacity = energyStore.getCurrentCapacity();
                if (storeCapacity == 0) continue;

                // set the draw per iteration to the highest possible value
                if (drawPerStore > storeCapacity) {
                    drawPerStore = storeCapacity;
                    nmbOfStores -= 1; // store is now empty, so it can't be drawn from
                }

                energyStore.setCurrentCapacity(storeCapacity - drawPerStore);
                drawnCapacity += drawPerStore;
            }
        }

        energyStoreRepository.saveAll(energyStores);

        return networkCapacity - amount;
    }

    public Iterable<EnergyStore> getStores(Long networkId) {
        Network network = networkRepository.findById(networkId).orElseThrow(() -> {
            String error = networkNotFoundMessage + networkId;
            logger.error(error);
            return new EntityNotFoundException(error);
        }); // TODO: not needed anymore, remove and handle error in different place

        return energyStoreRepository.findByNetwork(networkId);
    }
}
