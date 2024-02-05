package cz.inqool.eas.eil.subject.institution;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class SubjectInstitutionService extends DatedService<
        SubjectInstitution,
        SubjectInstitutionDetail,
        SubjectInstitutionList,
        SubjectInstitutionCreate,
        SubjectInstitutionUpdate,
        SubjectInstitutionRepository
        > {

    @Override
    protected void preCreateHook(SubjectInstitution object) {
        super.preCreateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    protected void preUpdateHook(SubjectInstitution object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }
}
