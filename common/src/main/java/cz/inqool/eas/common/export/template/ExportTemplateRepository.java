package cz.inqool.eas.common.export.template;

import cz.inqool.eas.common.dictionary.DictionaryRepository;
import cz.inqool.eas.common.dictionary.index.DictionaryIndex;
import cz.inqool.eas.common.dictionary.store.DictionaryStore;
import cz.inqool.eas.common.module.ModuleDefinition;

import java.util.List;

import static cz.inqool.eas.common.module.Modules.EXPORT;

public class ExportTemplateRepository extends DictionaryRepository<
        ExportTemplate,
        ExportTemplate,
        ExportTemplateIndexedObject,
        DictionaryStore<ExportTemplate, ExportTemplate, QExportTemplate>,
        DictionaryIndex<ExportTemplate, ExportTemplate, ExportTemplateIndexedObject>> {

    /**
     * Gets export templates with specified tag.
     *
     * @param tag Provided tag
     * @return List of found report templates
     */
    public  List<ExportTemplate> listByTags(String tag) {
        QExportTemplate model = QExportTemplate.exportTemplate;

        List<ExportTemplate> templates = query().
                select(model).
                from(model).
                where(model.deleted.isNull()).
                where(model.active.isTrue()).
                where(model.tags.contains(tag)).
                orderBy(model.created.asc())
                .fetch();

        getStore().detachAll();

        return templates;
    }

    @Override
    protected ModuleDefinition getModule() {
        return EXPORT;
    }
}
