package cz.inqool.eas.eil.keyword;

import cz.inqool.eas.common.domain.DomainService;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class KeywordService extends DomainService<
        Keyword,
        KeywordDetail,
        KeywordList,
        KeywordCreate,
        KeywordUpdate,
        KeywordRepository> {

    @Override
    protected void preCreateHook(Keyword object) {
        super.preCreateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    protected void preUpdateHook(Keyword object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }
}

