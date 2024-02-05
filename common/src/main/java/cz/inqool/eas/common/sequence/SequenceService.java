package cz.inqool.eas.common.sequence;

import cz.inqool.eas.common.dictionary.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

@Slf4j
public class SequenceService extends DictionaryService<
        Sequence,
        SequenceDetail,
        SequenceList,
        SequenceCreate,
        SequenceUpdate,
        SequenceRepository
        > {

    private SequenceGenerator generator;

    /**
     * Generated sequence value for sequence of given sequence ID.
     *
     * @param sequenceId sequence ID
     * @return generated value
     */
    @Transactional
    public String generateNextValue(String sequenceId) {
        return generator.generate(sequenceId);
    }

    /**
     * Generated sequence value for sequence of given sequence code.
     *
     * @param code sequence code
     * @return generated value or empty string if no sequence is found for the code
     */
    @Transactional
    public String generateNextValueByCode(String code) {
        Sequence sequence = repository.findByCode(Sequence.class, code);
        if (sequence == null) {
            log.warn("Failed to get sequence with code = '{}'. Returning empty string.", code);
            return "";
        }

        return generator.generate(sequence.getId());
    }

    @Autowired
    public void setGenerator(SequenceGenerator generator) {
        this.generator = generator;
    }
}
