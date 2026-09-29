package cz.inqool.eas.eil.subject.entry;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class SubjectEntryService extends DatedService<
        SubjectEntry,
        SubjectEntryDetail,
        SubjectEntryList,
        SubjectEntryCreate,
        SubjectEntryUpdate,
        SubjectEntryRepository
        > {

    @Override
    protected void preCreateHook(SubjectEntry object) {
        super.preCreateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    protected void preUpdateHook(SubjectEntry object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }
}
