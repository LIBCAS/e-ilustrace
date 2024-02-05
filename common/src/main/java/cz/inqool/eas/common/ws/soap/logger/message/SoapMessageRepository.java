package cz.inqool.eas.common.ws.soap.logger.message;

import cz.inqool.eas.common.authored.AuthoredRepository;
import cz.inqool.eas.common.authored.index.AuthoredIndex;
import cz.inqool.eas.common.authored.store.AuthoredStore;
import cz.inqool.eas.common.domain.DomainRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.List;

public class SoapMessageRepository extends AuthoredRepository<
        SoapMessage,
        SoapMessage,
        SoapMessageIndexedObject,
        AuthoredStore<SoapMessage, SoapMessage, QSoapMessage>,
        AuthoredIndex<SoapMessage, SoapMessage, SoapMessageIndexedObject>> {

    /**
     * @see DomainRepository#getReindexBatchSize()
     */
    @Getter
    @Value("${soap-logger.message.repository.reindex.batch-size:10000}")
    private int reindexBatchSize;


    public List<String> getFirstNIds(long limit) {
        QSoapMessage model = QSoapMessage.soapMessage;

        return query()
                .select(model.id)
                .from(model)
                .orderBy(model.created.asc())
                .limit(limit)
                .fetch();
    }

    public List<String> getFirstNIdsTill(long n, @NonNull Instant thresholdTime) {
        QSoapMessage model = QSoapMessage.soapMessage;

        return query()
                .select(model.id)
                .from(model)
                .where(model.created.before(thresholdTime))
                .orderBy(model.created.asc())
                .limit(n)
                .fetch();
    }

    public long countAllTill(@NonNull Instant thresholdTime) {
        QSoapMessage model = QSoapMessage.soapMessage;

        //noinspection ConstantConditions
        return query()
                .select(model.count())
                .from(model)
                .where(model.created.before(thresholdTime))
                .fetchOne();
    }
}
