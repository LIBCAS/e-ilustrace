package cz.inqool.eas.common.export.template;

import cz.inqool.eas.common.dictionary.DictionaryService;
import cz.inqool.eas.common.export.access.ExportAccessChecker;
import cz.inqool.eas.common.storage.file.FileManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportTemplateService extends DictionaryService<
        ExportTemplate,
        ExportTemplateDetail,
        ExportTemplateList,
        ExportTemplateCreate,
        ExportTemplateUpdate,
        ExportTemplateRepository
        > {

    private FileManager fileManager;

    private ExportAccessChecker accessChecker;

    @Override
    protected void preCreateHook(@NotNull ExportTemplate exportTemplate) {
        super.preCreateHook(exportTemplate);

        fileManager.preCreateHook(exportTemplate, ExportTemplate::getContent);
    }

    @Override
    protected void preUpdateHook(@NotNull ExportTemplate exportTemplate) {
        super.preUpdateHook(exportTemplate);

        ExportTemplate old = getInternal(ExportTemplate.class, exportTemplate.getId());
        fileManager.preUpdateHook(exportTemplate, old, ExportTemplate::getContent);
    }

    @Override
    protected void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);

        ExportTemplate exportTemplate = getInternal(ExportTemplate.class, id);
        fileManager.preDeleteHook(exportTemplate, ExportTemplate::getContent);
    }

    @Transactional
    public List<ExportTemplateList> listByTag(String tag) {
        List<ExportTemplate> templates = repository.listByTags(tag);

        Map<String, Boolean> accessMap = accessChecker.checkAccess(templates);

        return templates
                .stream()
                .filter(template -> accessMap.get(template.getId()))
                .map(ExportTemplateList::toView)
                .collect(Collectors.toList());
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired
    public void setAccessChecker(ExportAccessChecker accessChecker) {
        this.accessChecker = accessChecker;
    }
}
