package cz.inqool.eas.common.security.form.ldap;

import cz.inqool.eas.common.security.form.FormUserDetailProvider;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.ppolicy.PasswordPolicyException;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * Copy of {@link LdapAuthenticationProvider} with accessible authoritiesPopulator and implementing {@link FormUserDetailProvider}.
 */
public abstract class FormLdapAuthenticationProvider extends AbstractLdapAuthenticationProvider implements FormUserDetailProvider {

    protected LdapAuthenticator authenticator;

    protected LdapAuthoritiesPopulator authoritiesPopulator;

    private boolean hideUserNotFoundExceptions = true;

    /**
     * Create an instance with the supplied authenticator and authorities populator
     * implementations.
     * @param authenticator the authentication strategy (bind, password comparison, etc)
     * to be used by this provider for authenticating users.
     * @param authoritiesPopulator the strategy for obtaining the authorities for a given
     * user after they've been authenticated.
     */
    public FormLdapAuthenticationProvider(LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator, UserDetailsContextMapper contextMapper) {
        this.setAuthenticator(authenticator);
        this.setAuthoritiesPopulator(authoritiesPopulator);
        this.setUserDetailsContextMapper(contextMapper);
    }

    public FormLdapAuthenticationProvider() {
    }

    protected void setAuthenticator(LdapAuthenticator authenticator) {
        Assert.notNull(authenticator, "An LdapAuthenticator must be supplied");
        this.authenticator = authenticator;
    }

    protected LdapAuthenticator getAuthenticator() {
        return this.authenticator;
    }

    protected void setAuthoritiesPopulator(LdapAuthoritiesPopulator authoritiesPopulator) {
        Assert.notNull(authoritiesPopulator, "An LdapAuthoritiesPopulator must be supplied");
        this.authoritiesPopulator = authoritiesPopulator;
    }

    protected LdapAuthoritiesPopulator getAuthoritiesPopulator() {
        return this.authoritiesPopulator;
    }

    public void setHideUserNotFoundExceptions(boolean hideUserNotFoundExceptions) {
        this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
    }

    @Override
    protected DirContextOperations doAuthentication(UsernamePasswordAuthenticationToken authentication) {
        try {
            return getAuthenticator().authenticate(authentication);
        }
        catch (PasswordPolicyException ex) {
            // The only reason a ppolicy exception can occur during a bind is that the
            // account is locked.
            throw new LockedException(
                    this.messages.getMessage(ex.getStatus().getErrorCode(), ex.getStatus().getDefaultMessage()));
        }
        catch (UsernameNotFoundException ex) {
            if (this.hideUserNotFoundExceptions) {
                throw new BadCredentialsException(
                        this.messages.getMessage("LdapAuthenticationProvider.badCredentials", "Bad credentials"));
            }
            throw ex;
        }
        catch (NamingException ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    @Override
    protected Collection<? extends GrantedAuthority> loadUserAuthorities(DirContextOperations userData, String username,
                                                                         String password) {
        return getAuthoritiesPopulator().getGrantedAuthorities(userData, username);
    }

}