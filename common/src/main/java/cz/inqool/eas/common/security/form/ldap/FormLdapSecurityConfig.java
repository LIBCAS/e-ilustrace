package cz.inqool.eas.common.security.form.ldap;

import cz.inqool.eas.common.security.form.FormSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;

/**
 * TODO: comment
 */
@Slf4j
public abstract class FormLdapSecurityConfig extends FormSecurityConfig {
    @Bean
    public LdapContextSource ldapContextSource() {
        LdapContextSource contextSource = new LdapContextSource();

        contextSource.setUrl(this.getLdapUrl());
        contextSource.setUserDn(this.getLdapBindDn());
        contextSource.setPassword(this.getLdapBindPwd());
        contextSource.setBase(this.getLdapBaseDn());

        return contextSource;
    }

    @Bean
    public LdapUserSearch ldapUserSearch(LdapContextSource contextSource) {
        return new FilterBasedLdapUserSearch(this.getLdapUserBase(), this.getLdapUserFilter(), contextSource);
    }

    @Bean
    public BindAuthenticator ldapAuthenticator(LdapContextSource contextSource, LdapUserSearch userSearch) {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserSearch(userSearch);

        return authenticator;
    }

    protected abstract String getLdapUrl();
    protected abstract String getLdapBaseDn();
    protected abstract String getLdapBindDn();
    protected abstract String getLdapBindPwd();
    protected abstract String getLdapUserBase();
    protected abstract String getLdapUserFilter();
}
