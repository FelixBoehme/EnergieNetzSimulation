package felix.store;

import felix.network.Network;
import jakarta.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    public Float getMaxCapacity() {
        return maxCapacity;
    }

    public Float getCurrentCapacity() {
        return currentCapacity;
    }

    private void setCurrentCapacity(Float newCapacity) {
        this.currentCapacity = newCapacity;
    }

    public Network getNetwork() {
        return network;
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
            // TODO: error handling
        }
    }

    public void increaseCapacity(Float amount) {
        float newCapacity = currentCapacity + amount;

        if (newCapacity <= maxCapacity) {
            setCurrentCapacity(newCapacity);
        } else {
            String error = "Can't increase capacity of Store with ID " + id + ", by " + amount + ", because the result would exceed the Stores maximum capacity";
            throw new RuntimeException(error); // TODO replace with custom exception
        }
    }
}
