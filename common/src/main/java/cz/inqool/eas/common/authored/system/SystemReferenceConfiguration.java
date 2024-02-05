package cz.inqool.eas.common.authored.system;

import cz.inqool.eas.common.authored.user.UserReference;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Configuration for system reference subsystem.
 *
 * If application wants to use sequence subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 */
public abstract class SystemReferenceConfiguration {
    @PostConstruct
    public void init() {
        SystemGenerator.system = systemReference();
    }

    public abstract UserReference systemReference();
}
