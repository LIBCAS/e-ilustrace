package cz.inqool.eas.common.domain.index;

import cz.inqool.eas.common.domain.DomainRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Automatically check after startup if all indexes are
 * created and if not, create the missing ones.
 */
@Slf4j
@Component
@Order(0)
public class IndexInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private List<DomainRepository<?, ?, ?, ?, ?>> repositories;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (repositories != null) {
            log.info("Checking for missing indexes.");

            boolean atLeastOneFound = false;

            for (var repository : repositories) {
                if (!repository.isIndexManaged()) {
                    log.info("Skipping, because repository does not have managed index.");
                    continue;
                }

                if (!repository.isIndexInitialized()) {
                    atLeastOneFound = true;
                    log.info("Found missing index for {}.", repository);
                    log.info("Recreating missing index.");
                    repository.initIndex();
                }
            }

            if (atLeastOneFound) {
                log.info("All missing indexes were created.");
            } else {
                log.info("No missing indexes found.");
            }
        }
    }

    @Autowired(required = false)
    public void setRepositories(List<DomainRepository<?, ?, ?, ?, ?>> repositories) {
        this.repositories = repositories;
    }
}
