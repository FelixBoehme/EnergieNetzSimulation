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
        energyStoreService.softDeleteEnergyStore(storeId);

        assertThat(energyStore.getDeleted()).isEqualTo(true);
        verify(energyStoreRepository, times(1)).save(any());
    }

    @Test
    public void ensureNewStoreIsSaved() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        NewEnergyStore newEnergyStore = Mockito.mock(NewEnergyStore.class);
        EnergyStore energyStore = Mockito.mock(EnergyStore.class);
        Network network = Mockito.mock(Network.class);
        when(newEnergyStore.toEnergyStore()).thenReturn(energyStore);
        when(energyStore.getNetwork()).thenReturn(network);

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
        EnergyStore energyStore = Mockito.mock(EnergyStore.class);
        Network network = Mockito.mock(Network.class);
        when(networkRepository.findById(1L)).thenReturn(Optional.ofNullable(network));
        when(newEnergyStore.toEnergyStore(network)).thenReturn(energyStore);

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);
        energyStoreService.addEnergyStoreWithNetwork(newEnergyStore, 1L);

        verify(energyStoreRepository, times(1)).save(any());
    }

    @Test
    public void ensureNetworkCapacityUpdatedByUpdateCurrentCapacityChange() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStore energyStore = Mockito.mock(EnergyStore.class);
        Network network = Mockito.mock(Network.class);
        Long networkId = 1L;
        Float change = 500F;
        when(energyStoreRepository.findByIdActive(storeId)).thenReturn(Optional.ofNullable(energyStore));
        when(energyStore.getNetwork()).thenReturn(network);
        when(network.getId()).thenReturn(networkId);

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);
        energyStoreService.updateCurrentCapacity(storeId, change);

        verify(networkRepository, times(1)).updateCapacity(networkId, change, 0F);
    }

    @Test
    public void ensureNetworkCapacityUpdatedBySoftDeleteEnergyStoreWhenInNetwork() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStore energyStore = Mockito.mock(EnergyStore.class);
        Network network = Mockito.mock(Network.class);
        Long networkId = 1L;
        Float currentCapacity = 500F;
        Float maxCapacity = 500F;
        when(energyStoreRepository.findByIdActive(storeId)).thenReturn(Optional.ofNullable(energyStore));
        when(energyStore.getNetwork()).thenReturn(network);
        when(network.getId()).thenReturn(networkId);
        when(energyStore.getCurrentCapacity()).thenReturn(currentCapacity);
        when(energyStore.getMaxCapacity()).thenReturn(maxCapacity);

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);
        energyStoreService.softDeleteEnergyStore(storeId);

        verify(networkRepository, times(1)).updateCapacity(networkId, -currentCapacity, -maxCapacity);
    }

    @Test
    public void ensureNetworkCapacityNotUpdatedBySoftDeleteEnergyStoreWhenNotInNetwork() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        EnergyStore energyStore = Mockito.mock(EnergyStore.class);
        when(energyStoreRepository.findByIdActive(storeId)).thenReturn(Optional.ofNullable(energyStore));

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);
        energyStoreService.softDeleteEnergyStore(storeId);

        verify(networkRepository, times(0)).updateCapacity(any(), any(), any());
    }

    @Test
    void ensureNetworkCapacityUpdatedByAddEnergyStore() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        NewEnergyStore newEnergyStore = Mockito.mock(NewEnergyStore.class);
        EnergyStore energyStore = Mockito.mock(EnergyStore.class);
        Network network = Mockito.mock(Network.class);
        Long networkId = 1L;
        Float currentCapacity = 500F;
        Float maxCapacity = 500F;
        when(newEnergyStore.toEnergyStore()).thenReturn(energyStore);
        when(energyStore.getNetwork()).thenReturn(network);
        when(network.getId()).thenReturn(networkId);
        when(energyStore.getCurrentCapacity()).thenReturn(currentCapacity);
        when(energyStore.getMaxCapacity()).thenReturn(maxCapacity);

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);
        energyStoreService.addEnergyStore(newEnergyStore);

        verify(networkRepository, times(1)).updateCapacity(networkId, currentCapacity, maxCapacity);
    }

    @Test
    void ensureNetworkCapacityUpdatedByAddEnergyStoreWithNetwork() {
        NetworkRepository networkRepository = mock(NetworkRepository.class);
        EnergyStoreRepository energyStoreRepository = Mockito.mock(EnergyStoreRepository.class);
        NewEnergyStore newEnergyStore = Mockito.mock(NewEnergyStore.class);
        EnergyStore energyStore = Mockito.mock(EnergyStore.class);
        Network network = Mockito.mock(Network.class);
        Long networkId = 1L;
        Float currentCapacity = 500F;
        Float maxCapacity = 500F;
        when(networkRepository.findById(networkId)).thenReturn(Optional.ofNullable(network));
        when(newEnergyStore.toEnergyStore(network)).thenReturn(energyStore);
        when(energyStore.getCurrentCapacity()).thenReturn(currentCapacity);
        when(energyStore.getMaxCapacity()).thenReturn(maxCapacity);

        EnergyStoreService energyStoreService = new EnergyStoreService(networkRepository, energyStoreRepository);
        energyStoreService.addEnergyStoreWithNetwork(newEnergyStore, networkId);

        verify(networkRepository, times(1)).updateCapacity(networkId, currentCapacity, maxCapacity);
    }
}
