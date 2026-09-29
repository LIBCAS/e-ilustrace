package cz.inqool.eas.common.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public final class UserBuilder {
    private String id;
    private String name;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Tenant tenant;
    private boolean enabled;
    private String jsonData;

    private UserBuilder() {
    }

    public static UserBuilder anUser() {
        return new UserBuilder();
    }

    public UserBuilder id(String id) {
        this.id = id;
        return this;
    }

    public UserBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
        return this;
    }

    public UserBuilder tenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public UserBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public UserBuilder jsonData(String jsonData) {
        this.jsonData = jsonData;
        return this;
    }

    public User build() {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setAuthorities(authorities);
        user.setTenant(tenant);
        user.setEnabled(enabled);
        user.setJsonData(jsonData);
        return user;
    }
}
