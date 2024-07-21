package felix.filter;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

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
            } else {
                return builder.equal(propertyPath, criteria.getValue());
            }
        }
        return null;
    }
}