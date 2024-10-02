package felix.filter;

import felix.store.EnergyStoreType;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class GenericSpecification<T> implements Specification<T> {
    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        String[] path = criteria.getKey().split("\\.");
        Path<String> propertyPath = root.get(path[0]);
        for (int i = 1; i < path.length; i++) {
            propertyPath = propertyPath.get(path[i]);
        }

        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThan(propertyPath, criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThan(propertyPath, criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (propertyPath.getJavaType() == String.class) {
                return builder.like(builder.lower(propertyPath), "%" + criteria.getValue().toString().toLowerCase() + "%");
            } else if (criteria.getKey().equalsIgnoreCase("type")) {
                String value = criteria.getValue().toString();

                if (value.charAt(0) == '[') {
                    String[] options = value.replaceAll("[\\[\\]]", "").split(",");
                    List<EnergyStoreType> types = Arrays.stream(options).map(EnergyStoreType::valueOf).toList();
                    return propertyPath.in(types);
                }

                EnergyStoreType type = EnergyStoreType.valueOf(criteria.getValue().toString());
                return builder.equal(propertyPath, type);
            } else {
                return builder.equal(propertyPath, criteria.getValue());
            }
        }
        return null;
    }
}