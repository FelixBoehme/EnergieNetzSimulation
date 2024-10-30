package felix.network;

import felix.filter.SearchFilter;
import felix.store.EnergyStore;
import felix.store.EnergyStoreListDTO;
import felix.store.EnergyStoreNotFoundException;
import felix.store.draw.DrawBelowZeroException;
import felix.store.draw.NegativeDrawException;
import felix.filter.SpecificationBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: remove inconsistent use of response entities

@RestController
@RequestMapping("/api/network")
@RequiredArgsConstructor
@Slf4j
public class NetworkController {
    private final NetworkService networkService;

    @ExceptionHandler({NetworkNotFoundException.class, EnergyStoreNotFoundException.class})
    protected ResponseEntity<String> handleNotFound(RuntimeException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NetworkAlreadyExistsException.class, NegativeDrawException.class, DrawBelowZeroException.class, DeleteStoreFromNetworkMismatchException.class, StoreNotInNetworkException.class})
    protected ResponseEntity<String> handleBadRequest(RuntimeException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("{networkId}")
    @ResponseBody
    public Network getNetwork(@PathVariable("networkId") Long networkId) {
        return networkService.getNetwork(networkId);
    }

    @GetMapping("all")
    public Iterable<Network> getAllNetworks() {
        return networkService.getAllNetworks();
    }

    @GetMapping("{networkId}/stores")
    public EnergyStoreListDTO getStores(@PathVariable("networkId") Long networkId, Pageable pageable, @RequestParam(value = "search", required = false) String search) {
        Boolean hasFilter = search != null;
        SearchFilter<EnergyStore> searchFilter = new SearchFilter<EnergyStore>(search).where("network.id", networkId).where("deleted", false);
        Specification<EnergyStore> spec = searchFilter.build();
        return networkService.getStores(networkId, pageable, spec, hasFilter);
    }

    @PostMapping
    public ResponseEntity<Network> addNetwork(@Valid @RequestBody Network network) {
        networkService.addNetwork(network);

        return new ResponseEntity<>(network, HttpStatus.CREATED);
    }

    @PutMapping("{networkId}/energyStore/{energyStoreId}")
    public ResponseEntity<EnergyStore> addEnergyStore(@PathVariable("networkId") Long networkId, @PathVariable("energyStoreId") Long energyStoreId) {
        EnergyStore energyStore = networkService.addEnergyStore(networkId, energyStoreId);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }

    @PutMapping("{networkId}/capacity/{amount}")
    public Float drawCapacity(@PathVariable("networkId") Long networkId, @PathVariable("amount") Float amount) {
        return networkService.drawCapacity(networkId, amount, "fairDraw");
    }

    @DeleteMapping("{networkId}/energyStore/{energyStoreId}")
    public ResponseEntity<EnergyStore> deleteStoreFromNetwork(@PathVariable("networkId") Long networkId, @PathVariable("energyStoreId") Long energyStoreId) {
        EnergyStore energyStore = networkService.deleteStoreFromNetwork(networkId, energyStoreId);

        return new ResponseEntity<>(energyStore, HttpStatus.OK);
    }
}
