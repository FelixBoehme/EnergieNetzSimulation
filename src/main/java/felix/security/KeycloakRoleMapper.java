package felix.security;

import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Map;

public class KeycloakRoleMapper implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String REALM_ACCESS_KEY = "realm_access";
    private static final String ROLES_KEY = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {

        Object realmAccessRawData = source.getClaim(REALM_ACCESS_KEY);
        if (realmAccessRawData instanceof Map<?, ?>) {

            Map<?, ?> realmAccessItems = source.getClaim(REALM_ACCESS_KEY);
            if (realmAccessItems.containsKey(ROLES_KEY) && realmAccessItems.get(ROLES_KEY) instanceof List<?>) {

                List<String> roles = (List<String>) realmAccessItems.get(ROLES_KEY);
                List<KeycloakRole> rolesWithPrefix = roles.stream()
                        .map(role -> new KeycloakRole(ROLE_PREFIX + role))
                        .toList();

                return new JwtAuthenticationToken(source, rolesWithPrefix);
            }
        }

        return new JwtAuthenticationToken(source);
    }

    @Override
    public <U> Converter<Jwt, U> andThen(Converter<? super AbstractAuthenticationToken, ? extends U> after) {
        return Converter.super.andThen(after);
    }
}