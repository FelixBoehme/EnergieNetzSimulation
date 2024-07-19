package felix.network;

import felix.store.EnergyStore;
import felix.store.EnergyStoreListDTO;
import felix.store.EnergyStoreNotFoundException;
import felix.store.draw.DrawBelowZeroException;
import felix.store.draw.NegativeDrawException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public EnergyStoreListDTO getStores(@PathVariable("networkId") Long networkId, Pageable pageable) {
        return networkService.getStores(networkId, pageable);
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
