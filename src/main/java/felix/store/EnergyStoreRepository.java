package felix.store;

import felix.network.Network;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EnergyStoreRepository extends CrudRepository<EnergyStore, Long> {

    @Query("SELECT e FROM EnergyStore e WHERE e.deleted = FALSE")
    Iterable<EnergyStore> findAllActive();

    List<EnergyStore> findByNetworkAndDeletedFalseAndCurrentCapacityGreaterThanOrderByCurrentCapacityAsc(Network network, Integer currentCapacity);

    Iterable<EnergyStore> findByNetwork(Network network);

    @Query("SELECT e FROM EnergyStore e WHERE e.network IS NULL AND e.deleted = FALSE")
    Iterable<EnergyStore> findUnassigned();
}
