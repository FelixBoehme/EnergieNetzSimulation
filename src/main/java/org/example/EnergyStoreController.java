package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EnergyStoreController {

    @Autowired
    private EnergyStoreService energyStoreService;

    @GetMapping("/api/energyStore/{energyStoreId}")
    @ResponseBody
    public EnergyStore getEnergyStore(@PathVariable("energyStoreId") Long id) {
        return energyStoreService.getenergyStore(id);
    }

    @GetMapping("api/energyStore/active")
    @ResponseBody
    public Iterable<EnergyStore> getAllEnergyStores() {
        return energyStoreService.getActiveEnergyStores();
    }

    @PostMapping("api/energyStore")
    public ResponseEntity<EnergyStore> addEnergyStore(@RequestBody EnergyStore energyStore) {
        return  energyStoreService.addEnergyStore(energyStore);
    }

    @PostMapping("api/energyStore/network/{networkId}")
    public ResponseEntity<EnergyStore> addEnergyStoreWithNetwork(@RequestBody EnergyStore energyStore, @PathVariable("networkId") Long networkId) {
        return  energyStoreService.addEnergyStoreWithNetwork(energyStore, networkId);
    }

    @PutMapping("api/energyStore/{energyStoreId}/capacity/{change}")
    public ResponseEntity<EnergyStore> updateCurrentCapacity(@PathVariable("energyStoreId") Long energyStoreId, @PathVariable("change") Float change) {
        return energyStoreService.updateCurrentCapacity(energyStoreId, change);
    }

    @DeleteMapping("api/network/energyStore/{energyStoreId}")
    public ResponseEntity<EnergyStore> deleteEnergyStore(@PathVariable("energyStoreId") Long energyStoreId) {
        return energyStoreService.deleteEnergyStore(energyStoreId);
    }

    @DeleteMapping("api/energyStore/{energyStoreId}")
    public ResponseEntity<EnergyStore> softDeleteEnergyStore(@PathVariable("energyStoreId") Long energyStoreId) {
        return energyStoreService.softDeleteEnergyStore(energyStoreId);
    }
}
