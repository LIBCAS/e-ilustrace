package cz.inqool.eas.eil.theme;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class ThemeRepository extends DatedRepository<
        Theme,
        Theme,
        ThemeIndexedObject,
        DatedStore<Theme, Theme, QTheme>,
        DatedIndex<Theme, Theme, ThemeIndexedObject>> {
}
