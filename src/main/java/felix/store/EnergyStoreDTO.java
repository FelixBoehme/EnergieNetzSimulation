package felix.store;

import lombok.Getter;

public class EnergyStoreDTO {
    @Getter
    private Long id;
    @Getter
    private EnergyStoreType type;
    @Getter
    private Float currentCapacity;
    @Getter
    private Float maxCapacity;
    @Getter
    private String location;
    @Getter
    private Long networkId;
    @Getter
    private String networkName;

    public EnergyStoreDTO(Long id, EnergyStoreType type, Float currentCapacity, Float maxCapacity, String location, Long networkId, String networkName) {
        this.id = id;
        this.type = type;
        this.currentCapacity = currentCapacity;
        this.maxCapacity = maxCapacity;
        this.location = location;
        this.networkId = networkId;
        this.networkName = networkName;
    }
}
