package cz.inqool.eas.common.security;

import cz.inqool.eas.common.security.internal.EasAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;

public abstract class BaseSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private SessionRegistry sessionRegistry;

    private EasAccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement()
                .sessionConcurrency(c -> c.sessionRegistry(sessionRegistry))
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
            .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .and()
            .headers()
                .cacheControl()
                    .and()
                .frameOptions()
                    .disable()
                    .and()
            .authorizeRequests()
                .antMatchers(getApiDocsPathAntPattern()).permitAll() // allow swagger
                .antMatchers("/me").permitAll()                      // allow MeApi
                .and();
    }

    /**
     * Returns path to the OpenAPI docs sources (see {@code springdoc.api-docs.path} configuration property)
     */
    protected String getApiDocsPathAntPattern() {
        return "/api-docs/**";
    }

    @Autowired
    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Autowired
    public void setAccessDeniedHandler(EasAccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }
}
