package felix.store;

import felix.network.NetworkController;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
    public ResponseEntity<EnergyStore> addEnergyStore(@Valid @RequestBody NewEnergyStoreWithoutNetwork newEnergyStore) {
        return energyStoreService.addEnergyStore(newEnergyStore);
    }

    @PostMapping("api/energyStore/network/{networkId}")
    public ResponseEntity<EnergyStore> addEnergyStoreWithNetwork(@Valid @RequestBody NewEnergyStore newEnergyStore, @PathVariable("networkId") Long networkId) {
        return energyStoreService.addEnergyStoreWithNetwork(newEnergyStore, networkId); // TODO: check whether maxCapacity is above or equal to currentCapacity
    }

    @PutMapping("api/energyStore/{energyStoreId}/capacity/{change}")
    public ResponseEntity<EnergyStore> updateCurrentCapacity(@PathVariable("energyStoreId") Long energyStoreId, @PathVariable("change") Float change) {
        return energyStoreService.updateCurrentCapacity(energyStoreId, change);
    }

    // maybe rename to delete from network
    @DeleteMapping("api/energyStore/{energyStoreId}/network")
    public ResponseEntity<EnergyStore> deleteStoreFromNetwork(@PathVariable("energyStoreId") Long energyStoreId) {
        return energyStoreService.deleteStoreFromNetwork(energyStoreId);
    }

    @DeleteMapping("api/energyStore/{energyStoreId}")
    public ResponseEntity<EnergyStore> softDeleteEnergyStore(@PathVariable("energyStoreId") Long energyStoreId) {
        return energyStoreService.softDeleteEnergyStore(energyStoreId);
    }
}
