package cz.inqool.eas.eil.author;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import cz.inqool.eas.common.utils.AssertionUtils;
import cz.inqool.eas.eil.exhibition.Exhibition;
import cz.inqool.eas.eil.exhibition.ExhibitionEssential;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.exception.v2.ExceptionCode.OBJECT_ACCESS_DENIED;

@Service
public class AuthorService extends DatedService<
        Author,
        AuthorDetail,
        AuthorList,
        AuthorCreate,
        AuthorUpdate,
        AuthorRepository
        > {

    @Override
    protected void preCreateHook(Author object) {
        super.preCreateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    protected void preUpdateHook(Author object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }
}
