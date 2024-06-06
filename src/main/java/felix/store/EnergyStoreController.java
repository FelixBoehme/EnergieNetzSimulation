package felix.store;

import felix.network.NetworkController;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EnergyStoreController {

    @Autowired
    private EnergyStoreService energyStoreService;

    Logger logger = LoggerFactory.getLogger(NetworkController.class);

    @ExceptionHandler({EntityNotFoundException.class})
    protected ResponseEntity<String> handleNotFound(EntityNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // TODO: is this needed? probably will exist in some earlier response
    @GetMapping("/api/energyStore/{energyStoreId}")
    @ResponseBody
    public EnergyStore getEnergyStore(@PathVariable("energyStoreId") Long id) {
        return energyStoreService.getEnergyStore(id);
    }

    // TODO: is this needed? maybe only active and not in a netowork
    @GetMapping("api/energyStore/active")
    @ResponseBody
    public Iterable<EnergyStore> getAllEnergyStores() {
        return energyStoreService.getActiveEnergyStores();
    }

    @GetMapping("api/energyStore/unassigned")
    @ResponseBody
    public Iterable<EnergyStore> getUnassignedEnergyStores() {
        return energyStoreService.getUnassignedEnergyStores();
    }

    @PostMapping("api/energyStore")
    public ResponseEntity<EnergyStore> addEnergyStore(@RequestBody EnergyStore energyStore) {
        return energyStoreService.addEnergyStore(energyStore);
    }

    @PostMapping("api/energyStore/network/{networkId}")
    public ResponseEntity<EnergyStore> addEnergyStoreWithNetwork(@RequestBody EnergyStore energyStore, @PathVariable("networkId") Long networkId) {
        return energyStoreService.addEnergyStoreWithNetwork(energyStore, networkId);
    }

    @PutMapping("api/energyStore/{energyStoreId}/capacity/{change}")
    public ResponseEntity<EnergyStore> updateCurrentCapacity(@PathVariable("energyStoreId") Long energyStoreId, @PathVariable("change") Float change) {
        return energyStoreService.updateCurrentCapacity(energyStoreId, change);
    }

    // maybe rename to delete from network
    @DeleteMapping("api/energyStore/{energyStoreId}/network")
    public ResponseEntity<EnergyStore> deleteEnergyStore(@PathVariable("energyStoreId") Long energyStoreId) {
        return energyStoreService.deleteEnergyStore(energyStoreId);
    }

    @DeleteMapping("api/energyStore/{energyStoreId}")
    public ResponseEntity<EnergyStore> softDeleteEnergyStore(@PathVariable("energyStoreId") Long energyStoreId) {
        return energyStoreService.softDeleteEnergyStore(energyStoreId);
    }
}
