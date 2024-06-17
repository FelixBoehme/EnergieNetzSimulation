package felix.store;

import felix.network.Network;
import felix.network.NetworkNotFoundException;
import felix.network.NetworkRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EnergyStoreServiceTest {
    Long storeId = 1L;

    @Test
    public void throwExceptionWhenStoreNotFound() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);

        Exception exception = Assertions.assertThrows(EnergyStoreNotFoundException.class, () -> energyStoreService.getEnergyStore(storeId));

        assertThat(exception.getMessage()).isEqualTo(new EnergyStoreNotFoundException(storeId).getMessage());
    }

    /*@Test
    public void throwExceptionWhenStoreNotFoundWhileDeleting() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);

        Exception exception = Assertions.assertThrows(EnergyStoreNotFoundException.class, () -> networkService.deleteStoreFromNetwork(1L, storeId));

        assertThat(exception.getMessage()).isEqualTo(new EnergyStoreNotFoundException(storeId).getMessage());
        verify(energyStoreRepository, never()).save(any());
    }
    TODO: Move to NetworkServiceTest and test for throwing DeleteStoreFromNetworkMismatch
    */

    @Test
    public void throwExceptionWhenDecreasingCapacityBelowZero() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStore energyStore = new EnergyStore(EnergyStoreType.SOLAR, 0F, 0F, "Europahaus", null);
        when(energyStoreRepository.findByIdActive(storeId)).thenReturn(Optional.of(energyStore));

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);

        Float change = -500F;
        Exception exception = Assertions.assertThrows(NegativeChangeException.class, () ->
                energyStoreService.updateCurrentCapacity(storeId, change));

        assertThat(exception.getMessage()).isEqualTo(new NegativeChangeException(storeId, change).getMessage());
        verify(energyStoreRepository, never()).save(any());
    }

    @Test
    public void throwExceptionWhenIncreasingAboveMaxCapacity() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStore energyStore = new EnergyStore(storeId, EnergyStoreType.SOLAR, 0F, 0F, "Europahaus", null);
        when(energyStoreRepository.findByIdActive(storeId)).thenReturn(Optional.of(energyStore));

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);

        Float change = 500F;
        Exception exception = Assertions.assertThrows(MaxCapacityExceededException.class, () -> energyStoreService.updateCurrentCapacity(storeId, change));

        assertThat(exception.getMessage()).isEqualTo(new MaxCapacityExceededException(storeId, change).getMessage());
        verify(energyStoreRepository, never()).save(any());
    }

    @Test
    public void throwExceptionWhenStoreNotFoundWhileSoftDeleting() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);

        Exception exception = Assertions.assertThrows(EnergyStoreNotFoundException.class, () -> energyStoreService.softDeleteEnergyStore(storeId));

        assertThat(exception.getMessage()).isEqualTo(new EnergyStoreNotFoundException(storeId).getMessage());
        verify(energyStoreRepository, never()).save(any());
    }

    @Test
    public void ensureDeleteAttributeSetAndSaved() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStore energyStore = new EnergyStore(storeId, EnergyStoreType.SOLAR, 0F, 0F, "Europahaus", null);
        when(energyStoreRepository.findByIdActive(storeId)).thenReturn(Optional.of(energyStore));

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);

        EnergyStore resultEnergyStore = energyStoreService.softDeleteEnergyStore(storeId).getBody();

        assertThat(resultEnergyStore.getDeleted()).isEqualTo(true);
        verify(energyStoreRepository, times(1)).save(any());
    }

    @Test
    public void ensureNewStoreIsSaved() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        NewEnergyStoreWithoutNetwork newEnergyStore = Mockito.mock(NewEnergyStoreWithoutNetwork.class);

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);
        energyStoreService.addEnergyStore(newEnergyStore);

        verify(energyStoreRepository, times(1)).save(any());
    }

    @Test
    public void throwExceptionWhenNetworkNotFoundWhileAddingStore() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        NewEnergyStore newEnergyStore = Mockito.mock(NewEnergyStore.class);
        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);
        Long networkId = 1L;

        Exception exception = Assertions.assertThrows(NetworkNotFoundException.class, () -> energyStoreService.addEnergyStoreWithNetwork(newEnergyStore, networkId));

        assertThat(exception.getMessage()).isEqualTo(new NetworkNotFoundException(networkId).getMessage());
        verify(energyStoreRepository, never()).save(any());
    }

    @Test
    public void ensureNewStoreWithNetworkIsSaved() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        NewEnergyStore newEnergyStore = Mockito.mock(NewEnergyStore.class);
        Network network = Mockito.mock(Network.class);
        when(networkRepository.findById(1L)).thenReturn(Optional.ofNullable(network));

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);
        energyStoreService.addEnergyStoreWithNetwork(newEnergyStore, 1L);

        verify(energyStoreRepository, times(1)).save(any());
    }
}
