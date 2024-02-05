package cz.inqool.eas.eil.exhibition;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import cz.inqool.eas.common.domain.index.DomainIndexedObject;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.domain.index.dto.filter.NullFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.ForbiddenObject;
import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import cz.inqool.eas.common.utils.AssertionUtils;
import cz.inqool.eas.eil.exhibition.item.*;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import cz.inqool.eas.eil.user.User;
import cz.inqool.eas.eil.user.UserRef;
import cz.inqool.eas.eil.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.*;
import static cz.inqool.eas.common.utils.AssertionUtils.coalesce;

@Service
@Slf4j
public class ExhibitionService extends DatedService<
        Exhibition,
        ExhibitionDetail,
        ExhibitionList,
        ExhibitionCreate,
        ExhibitionUpdate,
        ExhibitionRepository> {
    private ExhibitionItemRepository exhibitionItemRepository;
    private UserRepository userRepository;

    @Transactional
    @Override
    public ExhibitionDetail create(ExhibitionCreate view) {
        UserChecker.checkUserHasAnyPermission(Permission.USER);
        User user = userRepository.find(UserChecker.getUserId());
        view.setUser(UserRef.toRef(user));
        ExhibitionDetail exhibition = super.create(view);
        ExhibitionRef exhibitionRef = ExhibitionRef.toRef(ExhibitionDetail.toEntity(exhibition));
        Set<ExhibitionItem> exhibitionItems = view.getItems().stream().map(item -> {
            item.setExhibition(exhibitionRef);
            AssertionUtils.notNull(item.getIllustration(), () -> new ForbiddenOperation(
                    ARGUMENT_VALUE_IS_NULL,
                    "Illustration in item " + item + " is null"));
            return ExhibitionItemCreate.toEntity(item);
        }).collect(Collectors.toSet());
        exhibitionItemRepository.create(exhibitionItems);
        return repository.find(ExhibitionDetail.class, exhibition.getId());
    }

    @Transactional
    @Override
    public ExhibitionDetail update(String id, ExhibitionUpdate view) {
        UserChecker.checkUserHasAnyPermission(Permission.USER);
        ExhibitionEssential exhibition = repository.findEssential(id);
        AssertionUtils.notNull(exhibition, () -> new ForbiddenOperation(
                ENTITY_NOT_EXIST,
                "Exhibition with id '" + id + "' does not exist"));

        AssertionUtils.eq(exhibition.getUser().id, UserChecker.getUserId(), () -> new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                .details(details -> details.clazz(Exhibition.class)));
        ExhibitionRef exhibitionRef = ExhibitionRef.toRef(ExhibitionDetail.toEntity(super.update(id, view)));
        Set<ExhibitionItem> createBatch = new HashSet<>();
        Set<ExhibitionItem> updateBatch = new HashSet<>();
        view.getItems().forEach(item -> {
            AssertionUtils.notNull(item.getIllustration(), () -> new ForbiddenOperation(
                    ARGUMENT_VALUE_IS_NULL,
                    "Illustration in item " + item + " is null"));
            item.setExhibition(exhibitionRef);
            if (item.getId() != null) {
                AssertionUtils.isTrue(item.getExhibition() == null || item.getExhibition().id.equals(id),
                        () -> new ForbiddenObject(
                                OBJECT_ACCESS_DENIED,
                        "Item " + item + " is already assigned to different Exhibition"));
                updateBatch.add(ExhibitionItemExternalUpdate.toEntity(item));
            } else {
                createBatch.add(ExhibitionItemExternalUpdate.toEntity(item));
            }
        });
        exhibitionItemRepository.create(createBatch);
        exhibitionItemRepository.update(updateBatch);
        return repository.find(ExhibitionDetail.class, id);
    }

    @Transactional
    public ExhibitionDetail deleteItems(String id, List<ExhibitionItemRef> items) {
        UserChecker.checkUserHasAnyPermission(Permission.USER);
        ExhibitionEssential exhibition = repository.findEssential(id);
        AssertionUtils.notNull(exhibition, () -> new ForbiddenOperation(
                ENTITY_NOT_EXIST,
                "Exhibition with id '" + id + "' does not exist"));
        AssertionUtils.eq(exhibition.getUser().id, UserChecker.getUserId(), () -> new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                .details(details -> details.clazz(Exhibition.class)));
        items.forEach(item -> exhibitionItemRepository.delete(item.id));
        return repository.find(ExhibitionDetail.class, id);
    }

    @Transactional
    public ExhibitionDetail publish(String id) {
        UserChecker.checkUserHasAnyPermission(Permission.USER);
        User user = userRepository.find(UserChecker.getUserId());
        Exhibition exhibition = repository.find(id);
        AssertionUtils.notNull(exhibition, () -> new ForbiddenOperation(
                ENTITY_NOT_EXIST,
                "Exhibition with id '" + id + "' does not exist"));
        AssertionUtils.eq(exhibition.getUser(), user, () -> new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                .details(details -> details.clazz(Exhibition.class)));
        exhibition.setPublished(exhibition.getPublished() != null ? null : Instant.now());
        repository.update(exhibition);
        return repository.find(ExhibitionDetail.class, id);
    }

    @Transactional
    public ExhibitionDetail duplicate(String id) {
        UserChecker.checkUserHasAnyPermission(Permission.USER);
        Exhibition exhibition = repository.find(id);
        AssertionUtils.notNull(exhibition, () -> new ForbiddenOperation(
                ENTITY_NOT_EXIST,
                "Exhibition with id '" + id + "' does not exist"));
        ExhibitionCreate exhibitionCreate = ExhibitionCreate.toView(exhibition);
        return create(exhibitionCreate);
    }

    public Result<ExhibitionList> listMine(Params params) {
        UserChecker.checkUserHasAnyPermission(Permission.USER);
        params = coalesce(params, Params::new);
        params.addFilter(new EqFilter(ExhibitionIndexedObject.IxFields.user + "." + DomainIndexedObject.Fields.id, UserChecker.getUserId()));
        params.addFilter(new NullFilter(DatedIndexedObject.Fields.deleted));

        return repository.listByParams(ExhibitionList.class, params);
    }

    @Override
    protected void preCreateHook(Exhibition object) {
        super.preCreateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.USER);
    }

    @Override
    protected void preUpdateHook(Exhibition object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.USER);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        ExhibitionEssential exhibition = repository.findEssential(id);
        AssertionUtils.notNull(exhibition, () -> new ForbiddenOperation(
                ENTITY_NOT_EXIST,
                "Exhibition with id '" + id + "' does not exist"));
        AssertionUtils.eq(exhibition.getUser().id, UserChecker.getUserId(), () -> new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                .details(details -> details.clazz(Exhibition.class)));
        exhibition.getItems().forEach(item -> exhibitionItemRepository.delete(item.getId()));
    }

    @Autowired
    public void setExhibitionItemRepository(ExhibitionItemRepository exhibitionItemRepository) {
        this.exhibitionItemRepository = exhibitionItemRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
