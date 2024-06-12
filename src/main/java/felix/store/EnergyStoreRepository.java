package felix.store;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EnergyStoreRepository extends CrudRepository<EnergyStore, Long> {

    @Query("SELECT e FROM EnergyStore e WHERE e.id = :storeId AND e.deleted = FALSE")
    Optional<EnergyStore> findByIdActive(Long storeId);

    @Query("SELECT e FROM EnergyStore e WHERE e.deleted = FALSE")
    Iterable<EnergyStore> findAllActive();

    @Query("SELECT e FROM EnergyStore e JOIN Network n ON e.network.id = :networkId WHERE e.currentCapacity >= 0 AND e.deleted = FALSE ORDER BY e.currentCapacity ASC")
    List<EnergyStore> findByNetworkPositiveCapacity(Long networkId);

    @Query("SELECT e FROM EnergyStore e JOIN Network n on e.network.id = :networkId WHERE e.currentCapacity >= 0 AND e.deleted = FALSE ORDER BY (e.currentCapacity / e.maxCapacity) ASC")
    List<EnergyStore> findByNetworkAscPercentage(Long networkId);

    @Query("SELECT e FROM EnergyStore e WHERE e.network.id = :networkId AND e.deleted = FALSE")
    Iterable<EnergyStore> findByNetwork(Long networkId);

    @Query("SELECT e FROM EnergyStore e WHERE e.network IS NULL AND e.deleted = FALSE")
    Iterable<EnergyStore> findUnassigned();

    @Query("SELECT COALESCE(currentCapacity, 0) AS currentCapacity, COALESCE(maxCapacity, 0) AS maxCapacity, CASE WHEN COALESCE(SUM(maxCapacity), 0) = 0 THEN 0 ELSE COALESCE(currentCapacity, 0)/COALESCE(maxCapacity, 0)  END AS percentageCapacity FROM (SELECT SUM(e.currentCapacity) AS currentCapacity, SUM(e.maxCapacity) AS maxCapacity FROM EnergyStore e WHERE e.network.id = :networkId) AS capacities")
    Map<String, Double> getCapacity(Long networkId); // TODO: use double instead of float project wide
}
