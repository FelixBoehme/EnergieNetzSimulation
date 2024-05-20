package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class NetworkController {

    @Autowired
    private NetworkService networkService;

    @GetMapping("api/network/{networkId}")
    @ResponseBody
    public Network getNetwork(@PathVariable("networkId") Long networkId) {
        return networkService.getNetwork(networkId);
    }

    @GetMapping("api/network/all")
    public Iterable<Network> getAllNetworks() {
        return networkService.getAllNetworks();
    }

    @GetMapping("api/network/{networkId}/capacity")
    public Map<String, Float> getCapacity(@PathVariable("networkId") Long networkId) {
        return networkService.getCapacity(networkId);
    }

    @PostMapping("api/network")
    public ResponseEntity<Network> addNetwork(@RequestBody Network network) {
        return networkService.addNetwork(network);
    }

    @PutMapping("api/network/{networkId}/energyStore/{energyStoreId}")
    public ResponseEntity<EnergyStore> addEnergyStore(@PathVariable("networkId") Long networkId, @PathVariable("energyStoreId") Long energyStoreId) {
        return networkService.addEnergyStore(networkId, energyStoreId);
    }

    @PutMapping("api/network/{networkId}/capacity/{amount}")
    public Float drawCapacity(@PathVariable("networkId") Long networkId, @PathVariable("amount") Float amount) {
        return networkService.drawCapacity(networkId, amount);
    }
}
