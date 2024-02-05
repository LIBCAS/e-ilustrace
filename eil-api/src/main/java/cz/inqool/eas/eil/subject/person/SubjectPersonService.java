package cz.inqool.eas.eil.subject.person;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class SubjectPersonService extends DatedService<
        SubjectPerson,
        SubjectPersonDetail,
        SubjectPersonList,
        SubjectPersonCreate,
        SubjectPersonUpdate,
        SubjectPersonRepository> {

    @Override
    protected void preCreateHook(SubjectPerson object) {
        super.preCreateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    protected void preUpdateHook(SubjectPerson object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }
}
