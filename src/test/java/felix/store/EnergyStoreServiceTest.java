package felix.store;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import felix.network.NetworkRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EnergyStoreServiceTest {

    @Test
    public void throwExceptionWhenDecreasingCapacityBelowZero() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStore energyStore = new EnergyStore();
        when(energyStoreRepository.findById(0L)).thenReturn(Optional.ofNullable(energyStore));

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);

        Exception exception = Assertions.assertThrows(RuntimeException.class, () ->
                energyStoreService.updateCurrentCapacity(0L, -500F));

        assertThat(exception.getMessage()).isEqualTo("can't reduce capacity below zero");
        verify(energyStoreRepository, never()).save(any());
    }
}
