package cz.inqool.eas.eil.download;

import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DownloadReferenceStore extends DomainStore<DownloadReference, DownloadReference, QDownloadReference> {

    public DownloadReferenceStore() {
        super(DownloadReference.class);
    }

    public DownloadReference getNext() {
        QDownloadReference model = QDownloadReference.downloadReference;

        DownloadReference downloadReference = query()
                .select(model)
                .from(model)
                .where(model.downloaded.isFalse())
                .where(model.failed.isFalse())
                .orderBy(model.created.asc())
                .fetchFirst();

        detachAll();
        return downloadReference;
    }

    public long countRemainingDownloads(int limit) {
        QDownloadReference model = QDownloadReference.downloadReference;

        long count = query()
                .select(model)
                .from(model)
                .where(model.downloaded.isFalse())
                .where(model.failed.isFalse())
                .limit(limit)
                .fetch()
                .size();

        detachAll();
        return count;
    }

    public List<DownloadReference> fetchFailedDownloads() {
        QDownloadReference model = QDownloadReference.downloadReference;

        List<DownloadReference> result = query()
                .select(model)
                .from(model)
                .where(model.downloaded.isFalse())
                .where(model.failed.isTrue())
                .fetch();

        detachAll();
        return result;
    }

    public List<DownloadReference> fetchForRecord(String recordId) {
        QDownloadReference model = QDownloadReference.downloadReference;

        List<DownloadReference> result = query()
                .select(model)
                .from(model)
                .where(model.record.id.eq(recordId))
                .fetch();

        detachAll();
        return result;
    }
}
