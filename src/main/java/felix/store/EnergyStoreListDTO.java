package felix.store;

import lombok.Getter;

import java.util.List;

public class EnergyStoreListDTO {
    @Getter
    private Long totalCount;

    @Getter
    private List<EnergyStoreDTO> stores;

    public EnergyStoreListDTO(Long totalCount, List<EnergyStoreDTO> stores) {
        this.totalCount = totalCount;
        this.stores = stores;
    }
}
