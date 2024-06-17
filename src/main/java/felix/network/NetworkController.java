package felix.network;

import felix.store.EnergyStore;
import felix.store.EnergyStoreNotFoundException;
import felix.store.draw.DrawBelowZeroException;
import felix.store.draw.NegativeDrawException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NetworkController {
    private final NetworkService networkService;

    Logger logger = LoggerFactory.getLogger(NetworkController.class);

    @ExceptionHandler({NetworkNotFoundException.class, EnergyStoreNotFoundException.class})
    protected ResponseEntity<String> handleNotFound(RuntimeException e) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NegativeDrawException.class, DrawBelowZeroException.class})
    protected ResponseEntity<String> handleBadRequest(RuntimeException e) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

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
    public Map<String, Double> getCapacity(@PathVariable("networkId") Long networkId) {
        return networkService.getCapacity(networkId);
    }

    @GetMapping("api/network/{networkId}/stores")
    public Iterable<EnergyStore> getStores(@PathVariable("networkId") Long networkId) {
        return networkService.getStores(networkId);
    }

    @PostMapping("api/network")
    public ResponseEntity<Network> addNetwork(@Valid  @RequestBody Network network) {
        return networkService.addNetwork(network);
    }

    @PutMapping("api/network/{networkId}/energyStore/{energyStoreId}")
    public ResponseEntity<EnergyStore> addEnergyStore(@PathVariable("networkId") Long networkId, @PathVariable("energyStoreId") Long energyStoreId) {
        return networkService.addEnergyStore(networkId, energyStoreId);
    }

    @PutMapping("api/network/{networkId}/capacity/{amount}") // maybe get because you get energy, but put is the correct action?
    public Float drawCapacity(@PathVariable("networkId") Long networkId, @PathVariable("amount") Float amount) {
        return networkService.drawCapacity(networkId, amount, "fairDraw");
    }
}
