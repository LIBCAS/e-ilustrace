package cz.inqool.eas.eil.iconclass;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class IconclassRepository extends DatedRepository<
        IconclassCategory,
        IconclassCategory,
        IconclassCategoryIndexedObject,
        DatedStore<IconclassCategory, IconclassCategory, QIconclassCategory>,
        DatedIndex<IconclassCategory, IconclassCategory, IconclassCategoryIndexedObject>> {

    public IconclassCategory findByCode(String code) {
        if (code == null) {
            return null;
        }
        QIconclassCategory model = QIconclassCategory.iconclassCategory;

        IconclassCategory category = query()
                .select(model)
                .from(model)
                .where(model.code.equalsIgnoreCase(code))
                .fetchOne();

        detachAll();
        return category;
    }
}
