package cz.inqool.eas.common.security.form.ldap;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.inqool.eas.common.security.User;
import lombok.SneakyThrows;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.InetOrgPerson;
import org.springframework.security.ldap.userdetails.InetOrgPersonContextMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;

public abstract class FormLdapContextMapper extends InetOrgPersonContextMapper {
    public static final String GIVEN_NAME_ATTR = "givenName";
    public static final String SUR_NAME_ATTR = "sn";
    public static final String EMPLOYEE_NUMBER_ATTR = "employeeNumber";
    /**
     * Maps the LDAP record into InetOrgPerson object and then to {@link User} class.
     *
     * todo: maps other attributes to JSON data
     */
    @SneakyThrows
    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        InetOrgPerson p = (InetOrgPerson) super.mapUserFromContext(ctx, username, authorities);

        User user = new User();
        user.setId(p.getUsername());
        user.setEmail(p.getMail());
        user.setName(p.getDisplayName());
        user.setEnabled(p.isAccountNonExpired() & p.isAccountNonLocked() & p.isCredentialsNonExpired() & p.isEnabled());

        Map<String, Object> data = new HashMap<>();
        data.put(GIVEN_NAME_ATTR, p.getGivenName());
        data.put(SUR_NAME_ATTR, p.getSn());
        data.put(EMPLOYEE_NUMBER_ATTR, p.getEmployeeNumber());
        data.putAll(this.mapOtherAttributes(ctx, username, authorities));

        ObjectMapper mapper = new ObjectMapper();
        String jsonData = mapper.writeValueAsString(data);
        user.setJsonData(jsonData);

        return additionalAuthenticationChecks(user, ctx);
    }

    protected Map<String, Object> mapOtherAttributes(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        return emptyMap();
    }

    /**
     * Add additional auth checks (e.g.: lookup in local DB) and/or storing local copy of user data.
     */
    protected abstract User additionalAuthenticationChecks(User user, DirContextOperations ctx) throws AuthenticationException;


    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        throw new UnsupportedOperationException();
    }
}
