package cz.inqool.eas.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * Singleton for accessing Spring application context from non-spring context.
 */
@Service
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext ctx;


    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        ctx = context;
    }

    /**
     * Gets the application context instance.
     */
    public static ApplicationContext getApplicationContext() {
        return ctx;
    }
}
