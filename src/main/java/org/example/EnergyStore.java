package org.example;

import jakarta.persistence.*;

// TODO: separate class for outputting values

@Entity
public class EnergyStore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private EnergyStoreType type;
    private Integer maxCapacity;
    private Float currentCapacity = 0F;
    private String location;
    private Boolean deleted = false;

    @ManyToOne
    @JoinColumn(nullable = true)
    private Network network;

    protected EnergyStore() {
    }

    public EnergyStore(EnergyStoreType type, Integer maxCapacity, Float currentCapacity, String location, Network network) {
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

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public Float getCurrentCapacity() {
        return currentCapacity;
    }

    public String getLocation() {
        return location;
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

    public void addCapacity() {

    }

    public void removeCapacity() {

    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void setCurrentCapacity(Float newCapacity) {
        this.currentCapacity = newCapacity;
    }
}
