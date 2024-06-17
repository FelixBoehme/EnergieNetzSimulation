package felix.store;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/energyStore")
@RequiredArgsConstructor
@Slf4j
public class EnergyStoreController {
    private final EnergyStoreService energyStoreService;

    @ExceptionHandler({EntityNotFoundException.class, EnergyStoreNotFoundException.class})
    protected ResponseEntity<String> handleNotFound(RuntimeException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NegativeChangeException.class, MaxCapacityExceededException.class})
    protected ResponseEntity<String> handleBadRequest(RuntimeException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NegativeCapacityException.class})
    protected ResponseEntity<String> handleNegativeCapacity(NegativeCapacityException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // TODO: is this needed? probably will exist in some earlier response
    @GetMapping("{energyStoreId}")
    @ResponseBody
    public EnergyStore getEnergyStore(@PathVariable("energyStoreId") Long id) {
        return energyStoreService.getEnergyStore(id);
    }

    @GetMapping("active")
    @ResponseBody
    public Iterable<EnergyStore> getAllEnergyStores() {
        return energyStoreService.getActiveEnergyStores();
    }

    @GetMapping("unassigned")
    @ResponseBody
    public Iterable<EnergyStore> getUnassignedEnergyStores() {
        return energyStoreService.getUnassignedEnergyStores();
    }

    @PostMapping
    public ResponseEntity<EnergyStore> addEnergyStore(@Valid @RequestBody NewEnergyStore newEnergyStore) {
        EnergyStore energyStore = energyStoreService.addEnergyStore(newEnergyStore);
        return new ResponseEntity<>(energyStore, HttpStatus.CREATED);
    }

    @PostMapping("network/{networkId}")
    public ResponseEntity<EnergyStore> addEnergyStoreWithNetwork(@Valid @RequestBody NewEnergyStore newEnergyStore, @PathVariable("networkId") Long networkId) {
        return energyStoreService.addEnergyStoreWithNetwork(newEnergyStore, networkId);
    }

    @PutMapping("{energyStoreId}/capacity/{change}")
    public ResponseEntity<EnergyStore> updateCurrentCapacity(@PathVariable("energyStoreId") Long energyStoreId, @PathVariable("change") Float change) {
        EnergyStore energyStore = energyStoreService.updateCurrentCapacity(energyStoreId, change);
        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    @DeleteMapping("{energyStoreId}")
    public ResponseEntity<EnergyStore> softDeleteEnergyStore(@PathVariable("energyStoreId") Long energyStoreId) {
        EnergyStore energyStore = energyStoreService.softDeleteEnergyStore(energyStoreId);
        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }
}
