package felix.filter;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SpecificationBuilder<T> {
    private final List<SearchCriteria> params;
    private final Set<String> protectedFields;

    public SpecificationBuilder(Set<String> protectedFields) {
        params = new ArrayList<>();
        this.protectedFields = protectedFields;
    }

    public void where(String key, Object value) {
        params.add(new SearchCriteria(key, ":", value));
        protectedFields.add(key);
    }

    public void where(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        protectedFields.add(key);
    }

    public void with(String key, String operation, Object value) {
        if (!protectedFields.contains(key))
            params.add(new SearchCriteria(key, operation, value));
    }

    public Specification<T> build() {
        if (params.isEmpty()) {
            return null;
        }

        Specification<T> result = new GenericSpecification<>(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new GenericSpecification<>(params.get(i)));
        }

        return result;
    }
}
