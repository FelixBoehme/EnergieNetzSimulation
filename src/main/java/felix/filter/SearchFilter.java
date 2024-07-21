package felix.filter;

import org.springframework.data.jpa.domain.Specification;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchFilter<T> {
    private final SpecificationBuilder<T> builder;
    private final Matcher matcher;

    public SearchFilter(String search, String[] protectedFields) {
        Set<String> protectedFieldsSet = new HashSet<String>(List.of(protectedFields));
        this.builder = new SpecificationBuilder<>(protectedFieldsSet);

        Pattern PATTERN = Pattern.compile("(\\w+(?:\\.\\w+)*?)([:<>])(\\w+?),");
        this.matcher = PATTERN.matcher(search + ",");
    }

    public SearchFilter(String search) {
        this(search, new String[]{});
    }

    public SearchFilter<T> where(String key, Object value) {
        builder.where(key, value);
        return this;
    }

    public Specification<T> build() {
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        return builder.build();
    }
}
