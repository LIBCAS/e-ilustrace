package cz.inqool.eas.common.security.form.twoFactor;

import cz.inqool.eas.common.utils.AssertionUtils;
import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.Session;

import java.time.Instant;

import static cz.inqool.eas.common.security.form.twoFactor.TwoFactorException.ErrorCode.*;

/**
 * Abstract class provides default implementation of two factor authentication
 **/
public abstract class TwoFactorService {

    public static final String TWO_FACTOR_PRE_AUTH_PERMISSION = "TWO_FACTOR_PRE_AUTH";

    protected TwoFactorRepository<? extends Session> twoFactorRepository;

    /**
     * Create new {@link TwoFactorSignIn} for given user
     *
     * - generate secret code and save it to session
     * - call {@link this#notifyUser(TwoFactorSignIn)}
     */
    public void createFor(@NonNull String userId) {
        TwoFactorSignIn signIn = new TwoFactorSignIn();
        signIn.setUserId(userId);
        signIn.setSecret(generateSecret());

        twoFactorRepository.saveToCurrentSession(signIn);

        notifyUser(signIn);
    }

    /**
     * Challenge {@link TwoFactorSignIn} with secret code
     */
    public Boolean challenge(String secret) {
        checkPreAuthPerm();

        TwoFactorSignIn signIn = twoFactorRepository.findCurrentTwoFactorSignIn();
        AssertionUtils.notNull(signIn, () -> new TwoFactorException(SIGN_IN_NOT_FOUND));

        checkExpirationTime(signIn);
        checkAttemptsLimit(signIn);

        if (signIn.getSecret().equals(secret)) {
            twoFactorRepository.removeTwoFactorAttribute();
            Authentication authentication = createAuthentication(signIn.getUserId());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } else {
            signIn.setAttempt(signIn.getAttempt() + 1);
            twoFactorRepository.saveToCurrentSession(signIn);
            return false;
        }
    }

    /**
     * Check that user is logged in and have {@link TwoFactorService#TWO_FACTOR_PRE_AUTH_PERMISSION} permission
     */
    private void checkPreAuthPerm() {
        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        if (contextAuth == null || contextAuth.getAuthorities().stream()
                .noneMatch(authority -> authority.getAuthority().equals(TWO_FACTOR_PRE_AUTH_PERMISSION))) {
            throw new TwoFactorException(MISSING_PRE_AUTH);
        }
    }

    private void checkExpirationTime(TwoFactorSignIn signIn) {
        Instant now = Instant.now();
        Instant lastAttemptPlusExpiration = signIn.getCreated().plusSeconds(getAttemptExpiration());

        if (now.isAfter(lastAttemptPlusExpiration)) {
            throw new TwoFactorException(signIn.getUserId(), SIGN_IN_EXPIRED);
        }
    }

    private void checkAttemptsLimit(TwoFactorSignIn signIn) {
        if (signIn.getAttempt() > getAttemptLimit()) {
            throw new TwoFactorException(signIn.getUserId(), NUMBER_OF_ATTEMPTS_EXCEEDED);
        }
    }

    /**
     * Generate secret code. Override this method for custom code generation.
     * Default implementation use
     * {@link org.apache.commons.lang3.RandomStringUtils#randomAlphanumeric(int)}
     */
    protected String generateSecret() {
        return RandomStringUtils.randomAlphanumeric(getSecretLength());
    }

    /**
     * Send notification to user with the secret secret
     */
    protected abstract void notifyUser(TwoFactorSignIn twoFactorSignIn);

    /**
     * Create Authentication for user with correct permissions
     * the Authentication will be set to current security context
     */
    protected abstract Authentication createAuthentication(String userId);

    /**
     * Length of secret code
     */
    public abstract Integer getSecretLength();

    /**
     * How long is two factor sign in valid in seconds
     */
    public abstract Integer getAttemptExpiration();

    /**
     * How many times can user attempt to challenge the secret
     */
    public abstract Integer getAttemptLimit();


    @Autowired
    public void setTwoFactorRepository(TwoFactorRepository<? extends Session> twoFactorRepository) {
        this.twoFactorRepository = twoFactorRepository;
    }
}
