package cz.inqool.eas.eil.genre;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class GenreService extends DatedService<
        Genre,
        GenreDetail,
        GenreList,
        GenreCreate,
        GenreUpdate,
        GenreRepository
        > {

    @Override
    protected void preCreateHook(Genre object) {
        super.preCreateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    protected void preUpdateHook(Genre object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }
}
