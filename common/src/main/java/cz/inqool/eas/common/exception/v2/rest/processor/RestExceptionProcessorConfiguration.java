package cz.inqool.eas.common.exception.v2.rest.processor;

import com.google.common.annotations.Beta;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Beta
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "eas.exception.handler.rest", name = "legacy", havingValue = "false")
public class RestExceptionProcessorConfiguration {

    @Bean
    @Scope(SCOPE_SINGLETON)
    @ConditionalOnMissingBean
    public ConstraintViolationExceptionProcessor constraintViolationExceptionProcessor() {
        return new ConstraintViolationExceptionProcessor();
    }

    @Bean
    @Scope(SCOPE_SINGLETON)
    @ConditionalOnMissingBean
    public DefaultExceptionProcessor defaultExceptionProcessor() {
        return new DefaultExceptionProcessor();
    }

    @Bean
    @Scope(SCOPE_SINGLETON)
    @ConditionalOnMissingBean
    public EasExceptionProcessor easExceptionProcessor() {
        return new EasExceptionProcessor();
    }

    @Bean
    @Scope(SCOPE_SINGLETON)
    @ConditionalOnMissingBean
    public GeneralExceptionProcessor generalExceptionProcessor() {
        return new GeneralExceptionProcessor();
    }

    @Bean
    @Scope(SCOPE_SINGLETON)
    @ConditionalOnMissingBean
    public MethodArgumentNotValidExceptionProcessor methodArgumentNotValidExceptionProcessor() {
        return new MethodArgumentNotValidExceptionProcessor();
    }
}
