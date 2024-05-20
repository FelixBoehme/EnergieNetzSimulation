package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NetworkService {

    @Autowired
    private EnergyStoreRepository energyStoreRepository;

    @Autowired
    private NetworkRepository networkRepository;

    public ResponseEntity<Network> addNetwork(Network network) {
        networkRepository.save(network);

        return new ResponseEntity<>(network, HttpStatus.CREATED);
    }

    public Network getNetwork(Long networkId) {
        return networkRepository.findById(networkId).orElseThrow(() -> new RuntimeException("not found"));
    }

    public ResponseEntity<EnergyStore> addEnergyStore(Long networkId, Long energyStoreId) {
        EnergyStore energyStore = energyStoreRepository.findById(energyStoreId).orElseThrow(() -> new RuntimeException("not found"));
        Network network = networkRepository.findById(networkId).orElseThrow(() -> new RuntimeException("not found"));
        energyStore.setNetwork(network);

        energyStoreRepository.save(energyStore);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    public Iterable<Network> getAllNetworks() {
        return networkRepository.findAll();
    }

    public Map<String, Float> getCapacity(Long networkId) {
        Network network = networkRepository.findById(networkId).orElseThrow(() -> new RuntimeException("not found"));

        Iterable<EnergyStore> energyStores = energyStoreRepository.findByNetwork(network);

        Float maxCapacitySum = 0F;
        float currentCapacitySum = 0F;
        float percentageCapactity = 0F;

        for (EnergyStore energyStore: energyStores) {
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
        Network network = networkRepository.findById(networkId).orElseThrow(() -> new RuntimeException("not found"));

        List<EnergyStore> energyStores = energyStoreRepository.findByNetworkAndDeletedFalseAndCurrentCapacityGreaterThanOrderByCurrentCapacityAsc(network, 0);

        float drawnCapacity = 0F;
        Float networkCapacity = getCapacity(networkId).get("currentCapacity");

        if (amount > networkCapacity) {
            throw new RuntimeException("not enough capacity in network");
        }

        int nmbOfStores = energyStores.size();

        while (drawnCapacity < amount){
            float drawPerStore = (amount - drawnCapacity) / nmbOfStores;

            for (EnergyStore energyStore: energyStores) {
                Float storeCapacity = energyStore.getCurrentCapacity();
                if (storeCapacity == 0) continue;

                // set the draw per iteration t the highest possible value
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
        Network network = networkRepository.findById(networkId).orElseThrow(() -> new RuntimeException("not found"));

        return energyStoreRepository.findByNetwork(network);
    }
}
