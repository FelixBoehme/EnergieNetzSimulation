package felix.store;

import felix.network.Network;
import jakarta.persistence.*;

// TODO: separate class for outputting values

@Entity
public class EnergyStore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private EnergyStoreType type;
    private Float maxCapacity;
    private Float currentCapacity;
    private String location;
    private Boolean deleted = false;

    @ManyToOne
    @JoinColumn(nullable = true)
    private Network network;

    protected EnergyStore() {
    }

    public EnergyStore(EnergyStoreType type, Float maxCapacity, Float currentCapacity, String location, Network network) {
        this.type = type;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.location = location;
        this.network = network;
    }

    public Long getId() {
        return id;
    }

    public EnergyStoreType getType() {
        return type;
    }

    public Float getMaxCapacity() {
        return maxCapacity;
    }

    public Float getCurrentCapacity() {
        return currentCapacity;
    }

    public String getLocation() {
        return location;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public Network getNetwork() {
        return network;
    }

    private void setCurrentCapacity(Float newCapacity) {
        this.currentCapacity = newCapacity;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void deleteFromNetwork() {
        network = null;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void drawCapacity(Float amount) {
        float newCapacity = currentCapacity - amount;

        if (newCapacity >= 0) {
            setCurrentCapacity(newCapacity);
        } else {
            throw new NegativeCapacityException(id, amount);
        }
    }

    public void increaseCapacity(Float amount) {
        float newCapacity = currentCapacity + amount;

        if (newCapacity <= maxCapacity) {
            setCurrentCapacity(newCapacity);
        } else {
            throw new MaxCapacityExceededException(id, amount);
        }
    }
}
