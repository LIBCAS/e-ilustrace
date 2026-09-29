package cz.inqool.eas.common.domain.index.reindex.reference;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

/**
 * Configuration for index reference update subsystem
 * <p>
 * If application wants to use this subsystem, it needs to extend this class and add {@link Configuration} annotation.
 * Also the {@link IndexReferenceSynchronizer} service must be implemented and exposed as a bean ({@link Service}).
 */
@EnableScheduling
public abstract class IndexReferenceConfiguration {

    @Bean
    @Scope(value = SCOPE_SINGLETON)
    public IndexReferenceHolder indexReferenceHolder() {
        return new IndexReferenceHolder();
    }

    @Bean
    public IndexReferenceScanner indexReferenceScanner(AutowireCapableBeanFactory beanFactory) {
        IndexReferenceScanner scanner = new IndexReferenceScanner();
        beanFactory.autowireBean(scanner);
        return scanner;
    }

    @Bean
    public IndexReferenceUpdateListener indexReferenceUpdateListener(AutowireCapableBeanFactory beanFactory) {
        IndexReferenceUpdateListener listener = new IndexReferenceUpdateListener();
        beanFactory.autowireBean(listener);
        return listener;
    }
}
