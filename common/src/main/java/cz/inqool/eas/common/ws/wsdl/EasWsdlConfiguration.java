package cz.inqool.eas.common.ws.wsdl;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class EasWsdlConfiguration {
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public EasWsdlDefinitionHandlerAdapter wsdlDefinitionHandlerAdapter() {
        return new EasWsdlDefinitionHandlerAdapter();
    }
}
