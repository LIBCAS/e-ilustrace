package cz.inqool.eas.common.sequence;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;

/**
 * Class with synchronized methods for generating sequence, classes are divided into Synchronized and Transactional
 * because of duplicities. Method are synchronized and requires a new transaction explicitly by annotation.
 * In order to be used, this annotation must be in separate method
 */
public class SequenceGenerator {
    private SequenceRepository repository;

    private PlatformTransactionManager transactionManager;

    /**
     * Retrieve latest sequence value as plain Long.
     *
     * @param sequenceId sequence code
     * @return latest sequence value
     */
    public synchronized Long generatePlain(@NotNull String sequenceId) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        return transactionTemplate.execute(status -> {
                    Sequence sequence = repository.find(sequenceId);
                    Long counter = sequence.getCounter();

                    sequence.setCounter(counter + 1);
                    repository.update(sequence);

                    return counter;
                }
        );
    }

    /**
     * Retrieve latest sequence value with applied formatting.
     *
     * @param sequenceId sequence code
     * @return formatted latest sequence value
     */
    @SneakyThrows
    public synchronized String generate(@NotNull String sequenceId) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        return transactionTemplate.execute(status -> {
            Sequence sequence = repository.find(sequenceId);
            Long counter = sequence.getCounter();

            sequence.setCounter(counter + 1);
            repository.update(sequence);

            DecimalFormat format = new DecimalFormat(sequence.getFormat());

            return format.format(counter);
        });
    }

    @SneakyThrows
    public synchronized String generateByCode(@NotNull String sequenceCode) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        return transactionTemplate.execute(status -> {
            Sequence sequence = repository.findByCode(Sequence.class, sequenceCode);
            Long counter = sequence.getCounter();

            sequence.setCounter(counter + 1);
            repository.update(sequence);

            DecimalFormat format = new DecimalFormat(sequence.getFormat());

            return format.format(counter);
        });
    }

    @Autowired
    public void setRepository(SequenceRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
