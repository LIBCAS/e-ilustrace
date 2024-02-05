package cz.inqool.eas.common.security.form;

import cz.inqool.eas.common.security.User;
import cz.inqool.eas.common.security.form.twoFactor.TwoFactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

public abstract class FormAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider implements FormUserDetailProvider {
    protected PasswordEncoder passwordEncoder;
    protected Boolean useTwoFactorAuth;
    protected TwoFactorService twoFactorService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String username = (String)authentication.getPrincipal();
        String password = (String)authentication.getCredentials();

        try {
            User user = this.findUser(username);
            notNull(user, () -> new BadCredentialsException("User not found"));

            boolean result = passwordEncoder.matches(password, user.getPassword());

            if (result != Boolean.TRUE) {
                throw new BadCredentialsException("Invalid password supplied");
            }

            if (useTwoFactorAuth && userDetails.getAuthorities().stream()
                    .anyMatch(ga -> ga.getAuthority().equals(TwoFactorService.TWO_FACTOR_PRE_AUTH_PERMISSION))) {
                twoFactorService.createFor(user.getId());
            }
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationServiceException("Error communicating with auth db", e);
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        User user;
        if (useTwoFactorAuth) {
            user = findUser(username);
            notNull(user, () -> new UsernameNotFoundException(username));
            //add permission to be able to challenge secret code
            user.setAuthorities(AuthorityUtils.createAuthorityList(TwoFactorService.TWO_FACTOR_PRE_AUTH_PERMISSION));
        } else {
            user = findUserWithAuthorities(username);
            notNull(user, () -> new UsernameNotFoundException(username));
        }

        return user;
    }

    /**
     * Should find user by given name WITHOUT authorities
     */
    public abstract User findUser(String username);

    /**
     * Should find user by given name WITH authorities
     */
    public User findUserWithAuthorities(String username) {
        return findUser(username);
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
    public void setUseTwoFactorAuth(@Value("${eas.security.form.two-factor.enabled:false}") Boolean useTwoFactorAuth) {
        this.useTwoFactorAuth = useTwoFactorAuth;
    }
    @Autowired(required = false)
    public void setTwoFactorService(TwoFactorService twoFactorService) {
        this.twoFactorService = twoFactorService;
    }
}
