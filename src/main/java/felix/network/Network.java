package felix.network;

import felix.store.EnergyStore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.Formula;

import java.util.List;

@Entity
public class Network {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Getter
    @NotNull
    @Column(unique = true)
    private String name;

    @Getter
    private Float currentCapacity = 0F;

    @Getter
    private Float maxCapacity = 0F;

    @Getter
    @Formula("CASE WHEN max_capacity = 0 THEN 0 ELSE (current_capacity / max_capacity) END")
    private Float percentageCapacity = 0F;

    @Getter
    private Long totalStores = 0L;

    @OneToMany(mappedBy = "network")
    private List<EnergyStore> energyStores;

    protected Network() {
    }

    public Network(String name) {
        this.name = name;
    }
}
