package cz.inqool.eas.eil.config.security;

import cz.inqool.eas.common.security.BaseSecurityConfiguration;
import cz.inqool.eas.eil.security.EilFormSecurityConfiguration;
import cz.inqool.eas.eil.security.Permission;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@Order(200)
@EnableWebSecurity
@Import({EilFormSecurityConfiguration.class})
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
public class EilSecurityConfiguration extends BaseSecurityConfiguration {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http
                .sessionManagement().maximumSessions(1).and().and()
                .authorizeRequests()
                .antMatchers("/stomp").permitAll()
                .antMatchers("/files/**").permitAll()
                .antMatchers("/selection/**").hasAuthority(Permission.USER.name())

                //security solved on service level
                .antMatchers("/author/**").permitAll()
                .antMatchers("/genre/**").permitAll()
                .antMatchers("/iconclass/**").permitAll()
                .antMatchers("/institution/**").permitAll()
                .antMatchers("/keyword/**").permitAll()
                .antMatchers("/publishing-place/**").permitAll()
                .antMatchers("/record/**").permitAll()
                .antMatchers("/subject/**").permitAll()
                .antMatchers("/theme/**").permitAll()
                .antMatchers("/user/**").permitAll()
                .antMatchers("/exhibition/**").permitAll()
                .antMatchers("/record-author/**").permitAll()

                // Administrative operations are allowed for ADMINISTRATOR
                .antMatchers("/import/**").hasAuthority(Permission.ADMIN.name())
                .antMatchers("/reindex").hasAuthority(Permission.ADMIN.name())
                .antMatchers("/vise/**").hasAuthority(Permission.ADMIN.name())

                .antMatchers("/internal/me/**").permitAll()
                .anyRequest().denyAll();
    }
}
