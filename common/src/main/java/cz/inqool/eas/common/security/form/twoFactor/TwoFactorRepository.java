package cz.inqool.eas.common.security.form.twoFactor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

/**
 * Class for storing {@link TwoFactorSignIn} information in session
 **/
@ConditionalOnProperty(prefix = "eas.security.form.two-factor", name = "enabled", havingValue = "true")
@Component
public class TwoFactorRepository<S extends Session> {

    private static final String TWO_FACTOR_SESSION_ATTRIBUTE = "TWO_FACTOR_AUTH";

    private SessionRepository<S> sessionRepository;
    private HttpSession currentSession;

    public TwoFactorSignIn findCurrentTwoFactorSignIn() {
        S session = findCurrentRedisSession();

        return session.getAttribute(TWO_FACTOR_SESSION_ATTRIBUTE);
    }

    public void saveToCurrentSession(TwoFactorSignIn signIn) {
        S session = findCurrentRedisSession();

        session.setAttribute(TWO_FACTOR_SESSION_ATTRIBUTE, signIn);
        sessionRepository.save(session);
    }

    public void removeTwoFactorAttribute() {
        S session = findCurrentRedisSession();

        session.removeAttribute(TWO_FACTOR_SESSION_ATTRIBUTE);
        sessionRepository.save(session);
    }

    private S findCurrentRedisSession() {
        notNull(currentSession, () -> new RuntimeException("Cannot find current http session."));
        S session = sessionRepository.findById(currentSession.getId());
        notNull(session, () -> new RuntimeException("Cannot find current redis session."));
        return session;
    }

    @Autowired
    public void setSessionRepository(SessionRepository<S> sessionRepository) {
        this.sessionRepository = sessionRepository;
    }
    @Autowired(required = false)
    public void setCurrentSession(HttpSession currentSession) {
        this.currentSession = currentSession;
    }
}
