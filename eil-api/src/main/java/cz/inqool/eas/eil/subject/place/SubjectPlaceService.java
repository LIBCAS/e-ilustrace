package cz.inqool.eas.eil.subject.place;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class SubjectPlaceService extends DatedService<
        SubjectPlace,
        SubjectPlaceDetail,
        SubjectPlaceList,
        SubjectPlaceCreate,
        SubjectPlaceUpdate,
        SubjectPlaceRepository> {

    @Override
    protected void preCreateHook(SubjectPlace object) {
        super.preCreateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    protected void preUpdateHook(SubjectPlace object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }
}
