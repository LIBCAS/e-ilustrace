package cz.inqool.eas.common.init;

import cz.inqool.eas.common.authored.user.CreatedByGenerator;
import cz.inqool.eas.common.authored.user.UpdatedByGenerator;
import cz.inqool.eas.common.differ.DifferModuleState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static cz.inqool.eas.common.utils.AopUtils.unwrap;

/**
 * Initializes database after startup. Creates predefined entities and mock data.
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 10)   //before migration and post init
public class DelegateDataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private List<DataInitializer> initializers;

    private PlatformTransactionManager transactionManager;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (initializers != null) {
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            try {
                CreatedByGenerator.bypassed.set(true);
                UpdatedByGenerator.bypassed.set(true);
                DifferModuleState.disable();

                initializers.forEach(initializer -> {
                    template.executeWithoutResult(status -> {
                        String initializerName = unwrap(initializer).getClass().getSimpleName();

                        log.info("Running data initializer {} in forward mode", initializerName);
                        try {
                            initializer.initialize();
                        } catch (Exception e) {
                            throw new RuntimeException("Initializer " + initializerName + " failed.", e);
                        }
                        log.info("Initializer {} has finished.", initializerName);
                    });
                });

                for (int i = initializers.size() - 1; i >= 0; i--) {
                    var initializer = initializers.get(i);

                    template.executeWithoutResult(status -> {
                        String initializerName = unwrap(initializer).getClass().getSimpleName();

                        log.info("Running data initializer {} in backward mode", initializerName);
                        try {
                            initializer.initializeBackward();
                        } catch (Exception e) {
                            throw new RuntimeException("Initializer " + initializerName + " failed.", e);
                        }
                        log.info("Initializer {} has finished.", initializerName);
                    });
                }

                log.info("Application initialized.");
            } finally {
                CreatedByGenerator.bypassed.set(false);
                UpdatedByGenerator.bypassed.set(false);

                DifferModuleState.enable();
            }
        } else {
            log.info("No data initializers found.");
        }
    }

    @Autowired(required = false)
    public void setInitializers(List<DataInitializer> initializers) {
        this.initializers = initializers;
    }

    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
