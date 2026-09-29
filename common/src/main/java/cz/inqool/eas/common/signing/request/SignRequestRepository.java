package cz.inqool.eas.common.signing.request;

import cz.inqool.eas.common.authored.AuthoredRepository;
import cz.inqool.eas.common.authored.index.AuthoredIndex;
import cz.inqool.eas.common.authored.store.AuthoredStore;
import cz.inqool.eas.common.module.ModuleDefinition;

import java.util.List;

import static cz.inqool.eas.common.module.Modules.SIGNING;

public class SignRequestRepository extends AuthoredRepository<
        SignRequest,
        SignRequest,
        SignRequestIndexedObject,
        AuthoredStore<SignRequest, SignRequest, QSignRequest>,
        AuthoredIndex<SignRequest, SignRequest, SignRequestIndexedObject>> {

    public List<SignRequest> listRequestsToSign(String userId) {
        QSignRequest request = QSignRequest.signRequest;

        List<SignRequest> requests = query().
                select(request).
                from(request).
                where(request.state.in(SignRequestState.NEW, SignRequestState.ERROR)).
                where(request.deleted.isNull()).
                where(request.user.id.eq(userId))
                .fetch();

        detachAll();

        return requests;
    }

    @Override
    protected ModuleDefinition getModule() {
        return SIGNING;
    }
}
