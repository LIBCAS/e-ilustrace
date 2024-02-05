package cz.inqool.eas.common.exception.v2.rest;

import cz.inqool.eas.common.exception.v2.ExceptionConfigurationProperties;
import cz.inqool.eas.common.exception.v2.ExceptionConfigurationProperties.HandlerProperties.RestProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@ConditionalOnProperty(prefix = "eas.exception.handler.rest", name = "legacy", havingValue = "false")
public class RestErrorAttributesConfiguration {

    private ServerProperties serverProperties;
    private RestProperties restProperties;


    /**
     * Overrides {@link ErrorMvcAutoConfiguration#errorAttributes} to provide own implementation
     */
    @Bean
    @Scope(SCOPE_SINGLETON)
    @ConditionalOnMissingBean
    public RestErrorAttributes errorAttributes() {
        return new RestErrorAttributes();
    }

    /**
     * Overrides {@link ErrorMvcAutoConfiguration#basicErrorController} to provide own implementation
     */
    @Bean
    @Scope(SCOPE_SINGLETON)
    @ConditionalOnMissingBean
    public BasicErrorController basicErrorController(ErrorAttributes errorAttributes,
            ObjectProvider<ErrorViewResolver> errorViewResolvers) {
        return new RestBasicErrorController(errorAttributes, this.serverProperties.getError(), this.restProperties,
                errorViewResolvers.orderedStream().collect(Collectors.toList()));
    }


    @Autowired
    public void setServerProperties(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Autowired
    public void setRestProperties(ExceptionConfigurationProperties properties) {
        this.restProperties = properties.getHandler().getRest();
    }
}
