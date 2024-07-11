package felix.network;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface NetworkRepository extends CrudRepository<Network, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Network n SET n.currentCapacity = n.currentCapacity + :currentCapacity, n.maxCapacity = n.maxCapacity + :maxCapacity WHERE n.id = :networkId")
    void updateCapacity(Long networkId, Float currentCapacity, Float maxCapacity);

    Boolean existsByName(String name);
}
