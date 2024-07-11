package felix.store;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EnergyStoreRepository extends CrudRepository<EnergyStore, Long> {

    @Query("SELECT e FROM EnergyStore e WHERE e.id = :storeId AND e.deleted = FALSE")
    Optional<EnergyStore> findByIdActive(Long storeId);

    @Query("SELECT e FROM EnergyStore e WHERE e.deleted = FALSE")
    List<EnergyStore> findAllActive();

    @Query("SELECT e FROM EnergyStore e JOIN Network n ON e.network.id = :networkId WHERE e.currentCapacity >= 0 AND e.deleted = FALSE ORDER BY e.currentCapacity ASC")
    List<EnergyStore> findByNetworkPositiveCapacity(Long networkId);

    @Query("SELECT e FROM EnergyStore e JOIN Network n on e.network.id = :networkId WHERE e.currentCapacity >= 0 AND e.deleted = FALSE ORDER BY (e.currentCapacity / e.maxCapacity) ASC")
    List<EnergyStore> findByNetworkAscPercentage(Long networkId);

    @Query("SELECT e FROM EnergyStore e WHERE e.network.id = :networkId AND e.deleted = FALSE")
    List<EnergyStore> findByNetwork(Long networkId);

    @Query("SELECT e FROM EnergyStore e WHERE e.network IS NULL AND e.deleted = FALSE")
    List<EnergyStore> findUnassigned();
}
