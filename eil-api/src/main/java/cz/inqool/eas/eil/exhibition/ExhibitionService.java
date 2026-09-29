package cz.inqool.eas.eil.exhibition;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import cz.inqool.eas.common.domain.index.DomainIndexedObject;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.domain.index.dto.filter.NullFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
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
        sortCreateItems(view.getItems());
        return super.create(view);
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

        sortUpdateItems(view.getItems());
        return super.update(id, view);
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
        Set<String> ids = items.stream().map(item -> item.id).collect(Collectors.toSet());
        List<ExhibitionItemExternalUpdate> keep = exhibition.getItems().stream()
                .filter(i -> !ids.contains(i.getId())).collect(Collectors.toList());
        exhibition.getItems().clear();
        exhibition.getItems().addAll(keep);
        sortUpdateItems(exhibition.getItems());
        repository.update(exhibition);
        return repository.find(ExhibitionDetail.class, id);
    }

    @Transactional
    public ExhibitionDetail publish(String id) {
        UserChecker.checkUserHasAnyPermission(Permission.USER);
        User user = userRepository.find(UserChecker.getUserId());
        Exhibition exhibition = repository.find(id);
        AssertionUtils.notNull(exhibition, () -> new ForbiddenOperation(
                ENTITY_NOT_EXIST, "Exhibition with id '" + id + "' does not exist"));
        if (!UserChecker.userHasAnyPermission(Permission.ADMIN)) {
            AssertionUtils.eq(exhibition.getUser().id, user.getId(), () -> new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                    .details(details -> details.clazz(Exhibition.class)));
        }
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

    public void sortCreateItems(List<ExhibitionItemCreate> items) {
        int order = 0;
        for (ExhibitionItemCreate item : items) {
            item.setOrder(order);
            order++;
        }
    }

    public void sortUpdateItems(List<ExhibitionItemExternalUpdate> items) {
        int order = 0;
        for (ExhibitionItemExternalUpdate item : items) {
            item.setOrder(order);
            order++;
        }
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
        if (!UserChecker.userHasAnyPermission(Permission.SUPER_ADMIN)) {
            AssertionUtils.eq(exhibition.getUser().id, UserChecker.getUserId(), () -> new ForbiddenOperation(OBJECT_ACCESS_DENIED)
                    .details(details -> details.clazz(Exhibition.class)));
        }
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
