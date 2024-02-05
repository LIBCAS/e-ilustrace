package cz.inqool.eas.common.ws.soap.logger.message;

import cz.inqool.eas.common.authored.AuthoredService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Supplier;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

@Slf4j
public class SoapMessageService extends AuthoredService<
        SoapMessage,
        SoapMessageDetail,
        SoapMessageList,
        SoapMessageCreate,
        SoapMessageUpdate,
        SoapMessageRepository
        > {

    @Setter
    private Long soapMessageMaxLimit;
    @Setter
    private Long deleteAfterValue;
    @Setter
    private ChronoUnit deleteAfterUnit;


    @Scheduled(cron = "${soap-logger.message.delete.cron:-}")
    public void autoDelete() {
        log.trace("Deleting old SOAP messages...");
        deleteOldRecords();
        deleteOverLimitRecords();
        log.trace("Deleting old SOAP messages finished.");
    }

    private void deleteOldRecords() {
        if (deleteAfterValue != null && deleteAfterUnit != null) {
            Duration maxDuration = deleteAfterUnit.getDuration().multipliedBy(deleteAfterValue);
            Instant thresholdTime = Instant.now().minus(maxDuration);

            log.debug("Finding soap messages older than {}...", thresholdTime);

            final long olderCount = repository.countAllTill(thresholdTime);
            if (olderCount > 0) {
                log.info("Deleting {} old soap message logs...", olderCount);

                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                transactionTemplate.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);

                Supplier<List<String>> nextOldIdBatchGetter = () -> repository.getFirstNIdsTill(1000, thresholdTime);

                int deletedCounter = 0;
                List<String> oldLogIdsBatch = nextOldIdBatchGetter.get();
                while (!oldLogIdsBatch.isEmpty()) {
                    log.trace("Deleting {} items in batch ({}/{})...", oldLogIdsBatch.size(), deletedCounter, olderCount);
                    var deleted = transactionTemplate.execute(s -> repository.deletePermanently(oldLogIdsBatch));
                    //noinspection ConstantConditions
                    deletedCounter += deleted.size();

                    oldLogIdsBatch.clear();
                    oldLogIdsBatch.addAll(nextOldIdBatchGetter.get());
                }

                log.info("Soap messages deleted: {}", deletedCounter);
            }
        } else {
            log.trace("Value '{}' or '{}' is not set, skipping.", "deleteAfterValue", "deleteAfterUnit");
        }
    }

    private void deleteOverLimitRecords() {
        if (soapMessageMaxLimit != null) {
            long count = repository.countAll();
            log.trace("Found {} soap messages.", count);

            if (count > soapMessageMaxLimit) {
                final int overLimitCount = (int) (count - soapMessageMaxLimit);
                log.info("Found {} soap messages over limit.", overLimitCount);

                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                transactionTemplate.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);

                int deletedCounter = 0;
                int remaining = overLimitCount;
                while (remaining > 0) {
                    List<String> ids = repository.getFirstNIds(1000);
                    if (ids.isEmpty()) {
                        log.error("Should load {} items, but the result is already empty.", remaining);
                        break;
                    }

                    if (ids.size() >= remaining) {
                        ids = ids.subList(0, remaining);
                    }

                    log.trace("Deleting {} items in batch ({}/{})...", ids.size(), deletedCounter, overLimitCount);
                    final List<String> idsToDelete = ids;
                    transactionTemplate.executeWithoutResult(s -> repository.deletePermanently(idsToDelete));
                    deletedCounter += idsToDelete.size();
                    remaining -= idsToDelete.size();
                }
            } else {
                log.trace("No soap messages over limit.");
            }
        } else {
            log.trace("Value '{}' is not set, skipping.", "soapMessageMaxLimit");
        }
    }
}
