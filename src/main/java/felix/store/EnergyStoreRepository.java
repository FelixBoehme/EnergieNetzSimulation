package felix.store;

import felix.network.Network;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EnergyStoreRepository extends CrudRepository<EnergyStore, Long> {

    @Query("SELECT e FROM EnergyStore e WHERE e.id = :storeId AND e.deleted = FALSE")
    Optional<EnergyStore> findByIdActive(Long storeId);

    @Query("SELECT e FROM EnergyStore e WHERE e.deleted = FALSE")
    Iterable<EnergyStore> findAllActive();

    @Query("SELECT e FROM EnergyStore e LEFT JOIN Network n ON e.network.id = :networkId WHERE e.currentCapacity >= 0 AND e.deleted = FALSE ORDER BY e.currentCapacity ASC")
    List<EnergyStore> findByNetworkPositiveCapacity(Long networkId);

    @Query("SELECT e FROM EnergyStore e WHERE e.network.id = :networkId AND e.deleted = FALSE")
    Iterable<EnergyStore> findByNetwork(Long networkId);

    @Query("SELECT e FROM EnergyStore e WHERE e.network IS NULL AND e.deleted = FALSE")
    Iterable<EnergyStore> findUnassigned();
}
