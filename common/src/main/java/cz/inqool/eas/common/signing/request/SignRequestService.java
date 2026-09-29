package cz.inqool.eas.common.signing.request;

import com.google.common.base.Objects;
import cz.inqool.eas.common.authored.AuthoredService;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.exception.v2.ForbiddenObject;
import cz.inqool.eas.common.exception.v2.InvalidArgument;
import cz.inqool.eas.common.exception.v2.MissingAttribute;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.common.signing.request.dto.UploadSignedContentDto;
import cz.inqool.eas.common.signing.request.event.SignCanceledEvent;
import cz.inqool.eas.common.signing.request.event.SignCompletedEvent;
import cz.inqool.eas.common.signing.request.event.SignErrorEvent;
import cz.inqool.eas.common.storage.file.File;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.*;
import static cz.inqool.eas.common.utils.AssertionUtils.eq;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

public class SignRequestService extends AuthoredService<
        SignRequest,
        SignRequestDetail,
        SignRequestList,
        SignRequestCreate,
        SignRequestUpdate,
        SignRequestRepository
        > {

    @Override
    protected void preCreateHook(@NotNull SignRequest object) {
        super.preCreateHook(object);

        object.setState(SignRequestState.NEW);
    }

    @Transactional
    public SignRequest enqueue(String name, String identifier, UserReference user, Set<File> toSign) {
        SignRequest request = new SignRequest();
        request.setName(name);
        request.setIdentifier(identifier);

        LinkedHashSet<SignContent> contents = new LinkedHashSet<>();
        for (File file : toSign) {
            SignContent content = new SignContent();
            content.setToSign(file);
            content.setOrder(contents.size());
            contents.add(content);
        }

        request.setContents(contents);
        request.setUser(user);

        this.createInternal(request);
        return request;
    }

    @Transactional
    public void cancel(String id, String reason) {
        SignRequest request = repository.find(id);
        notNull(request, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", SignRequest.class.getSimpleName()))
                .debugInfo(info -> info.clazz(SignRequest.class))
                .logAll());
        eq(request.getState(), SignRequestState.NEW, () -> new ForbiddenObject(SIGN_REQUEST_ALREADY_PROCESSED)
                .details(details -> details
                        .id(id)
                        .property("type", SignRequest.class.getSimpleName())
                        .property("expectedState", SignRequestState.NEW)
                        .property("actualState", request.getState()))
                .debugInfo(info -> info.clazz(SignRequest.class)));

        request.setState(SignRequestState.CANCELED);
        request.setError(reason);
        repository.update(request);

        this.eventPublisher.publishEvent(new SignCanceledEvent(this, request));
    }

    @Transactional
    public void error(String id, String error) {
        SignRequest request = repository.find(id);
        notNull(request, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", SignRequest.class.getSimpleName()))
                .debugInfo(info -> info.clazz(SignRequest.class))
                .logAll());
        eq(request.getState(), SignRequestState.NEW, () -> new ForbiddenObject(SIGN_REQUEST_ALREADY_PROCESSED)
                .details(details -> details
                        .id(id)
                        .property("type", SignRequest.class.getSimpleName())
                        .property("expectedState", SignRequestState.NEW)
                        .property("actualState", request.getState()))
                .debugInfo(info -> info.clazz(SignRequest.class)));

        request.setState(SignRequestState.ERROR);
        request.setError(error);
        repository.update(request);

        this.eventPublisher.publishEvent(new SignErrorEvent(this, request));
    }

    @Transactional
    public void uploadSignedFile(String requestId, UploadSignedContentDto dto) {
        notNull(dto, () -> new InvalidArgument(ARGUMENT_VALUE_IS_NULL)
                .debugInfo(info -> info.name("dto")));
        notNull(dto.getContent(), () -> new MissingAttribute(ATTRIBUTE_VALUE_IS_NULL)
                .details(details -> details.property("content")));
        notNull(dto.getSigned(), () -> new MissingAttribute(ATTRIBUTE_VALUE_IS_NULL)
                .details(details -> details.property("signed")));

        uploadSignedFile(requestId, dto.getContent().getId(), dto.getSigned());
    }

    @Transactional
    public void uploadSignedFile(String requestId, String contentId, File signed) {
        SignRequest request = repository.find(requestId);
        notNull(request, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(requestId).property("type", SignRequest.class.getSimpleName()))
                .debugInfo(info -> info.clazz(SignRequest.class))
                .logAll());
        eq(request.getState(), SignRequestState.NEW, () -> new ForbiddenObject(SIGN_REQUEST_ALREADY_PROCESSED)
                .details(details -> details
                        .id(requestId)
                        .property("type", SignRequest.class.getSimpleName())
                        .property("expectedState", SignRequestState.NEW)
                        .property("actualState", request.getState()))
                .debugInfo(info -> info.clazz(SignRequest.class)));

        SignContent content = request.getContents()
                .stream()
                .filter(c -> Objects.equal(c.getId(), contentId))
                .findFirst()
                .orElse(null);
        notNull(content, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(contentId).property("type", SignContent.class.getSimpleName()))
                .debugInfo(info -> info.clazz(SignContent.class))
                .logAll());

        content.setSigned(signed);

        repository.update(request);
    }

    @Transactional
    public void sign(String id) {
        SignRequest request = repository.find(id);
        notNull(request, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", SignRequest.class.getSimpleName()))
                .debugInfo(info -> info.clazz(SignRequest.class))
                .logAll());
        eq(request.getState(), SignRequestState.NEW, () -> new ForbiddenObject(SIGN_REQUEST_ALREADY_PROCESSED)
                .details(details -> details
                        .id(id)
                        .property("type", SignRequest.class.getSimpleName())
                        .property("expectedState", SignRequestState.NEW)
                        .property("actualState", request.getState()))
                .debugInfo(info -> info.clazz(SignRequest.class)));

        request.setState(SignRequestState.SIGNED);
        repository.update(request);

        this.eventPublisher.publishEvent(new SignCompletedEvent(this, request));
    }

    public List<SignRequest> listRequestsToSign(String userId) {
        return repository.listRequestsToSign(userId);
    }
}
