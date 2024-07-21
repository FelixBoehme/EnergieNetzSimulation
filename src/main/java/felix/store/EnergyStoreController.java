package felix.store;

import felix.filter.SearchFilter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public EnergyStoreListDTO getAllEnergyStores(Pageable pageable, @RequestParam(value = "search", required = false) String search) {
        SearchFilter<EnergyStore> searchFilter = new SearchFilter<EnergyStore>(search).where("deleted", false);
        Specification<EnergyStore> spec = searchFilter.build();
        return energyStoreService.getActiveEnergyStores(pageable, spec);
    }

    @GetMapping("unassigned")
    @ResponseBody
    public List<EnergyStoreDTO> getUnassignedEnergyStores() {
        return energyStoreService.getUnassignedEnergyStores();
    }

    @PostMapping
    public ResponseEntity<EnergyStoreDTO> addEnergyStore(@Valid @RequestBody NewEnergyStore newEnergyStore) {
        EnergyStoreDTO energyStoreDTO = energyStoreService.addEnergyStore(newEnergyStore).toDTO();
        return new ResponseEntity<>(energyStoreDTO, HttpStatus.CREATED);
    }

    @PostMapping("network/{networkId}")
    public ResponseEntity<EnergyStoreDTO> addEnergyStoreWithNetwork(@Valid @RequestBody NewEnergyStore newEnergyStore, @PathVariable("networkId") Long networkId) {
        EnergyStoreDTO energyStoreDTO = energyStoreService.addEnergyStoreWithNetwork(newEnergyStore, networkId).toDTO();
        return new ResponseEntity<>(energyStoreDTO, HttpStatus.OK);
    }

    @PutMapping("{energyStoreId}/capacity/{change}")
    public ResponseEntity<EnergyStoreDTO> updateCurrentCapacity(@PathVariable("energyStoreId") Long energyStoreId, @PathVariable("change") Float change) {
        EnergyStoreDTO energyStoreDTO = energyStoreService.updateCurrentCapacity(energyStoreId, change).toDTO();
        return new ResponseEntity<>(energyStoreDTO, HttpStatus.OK);
    }

    @DeleteMapping("{energyStoreId}")
    public ResponseEntity<EnergyStoreDTO> softDeleteEnergyStore(@PathVariable("energyStoreId") Long energyStoreId) {
        EnergyStoreDTO energyStoreDTO = energyStoreService.softDeleteEnergyStore(energyStoreId).toDTO();
        return new ResponseEntity<>(energyStoreDTO, HttpStatus.OK);
    }
}
