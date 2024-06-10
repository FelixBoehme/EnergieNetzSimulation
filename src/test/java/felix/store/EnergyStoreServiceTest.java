package felix.store;

import felix.network.NetworkRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EnergyStoreServiceTest {

    @Test
    public void throwExceptionWhenStoreNotFound() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> energyStoreService.getEnergyStore(0L));

        assertThat(exception.getMessage()).is("Couldn't find Store with ID: 0");
    }

    @Test
    public void throwExceptionWhenDecreasingCapacityBelowZero() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStore energyStore = new EnergyStore(EnergyStoreType.SOLAR, 0F, 0F, "Europahaus", null);
        when(energyStoreRepository.findByIdActive(0L)).thenReturn(Optional.of(energyStore));

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                energyStoreService.updateCurrentCapacity(0L, -500F));

        assertThat(exception.getMessage()).isEqualTo(new ResponseStatusException(HttpStatus.BAD_REQUEST).getMessage());
        verify(energyStoreRepository, never()).save(any());
    }

    @Test
    public void throwExceptionWhenIncreasingAboveMaxCapacity() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStore energyStore = new EnergyStore(EnergyStoreType.SOLAR, 0F, 0F, "Europahaus", null);
        when(energyStoreRepository.findByIdActive(0L)).thenReturn(Optional.of(energyStore));

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class, () -> energyStoreService.updateCurrentCapacity(0L, 500F));

        assertThat(exception.getMessage()).isEqualTo(new ResponseStatusException(HttpStatus.BAD_REQUEST).getMessage());
        verify(energyStoreRepository, never()).save(any());
    }
}
