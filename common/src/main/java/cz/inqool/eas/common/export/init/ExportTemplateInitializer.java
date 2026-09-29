package cz.inqool.eas.common.export.init;

import cz.inqool.eas.common.export.init.dto.ExportTemplateDto;
import cz.inqool.eas.common.export.init.dto.ExportTemplatesDto;
import cz.inqool.eas.common.export.template.ExportTemplate;
import cz.inqool.eas.common.export.template.ExportTemplateRepository;
import cz.inqool.eas.common.export.template.ExportType;
import cz.inqool.eas.common.storage.file.FileManager;
import cz.inqool.eas.common.utils.JsonUtils;
import cz.inqool.eas.common.utils.ResourceReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ExportTemplateInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private ExportTemplateRepository repository;

    private FileManager fileManager;

    private final boolean enabled;
    private final Resource initFile;

    public ExportTemplateInitializer(boolean enabled, Resource initFile) {
        this.enabled = enabled;
        this.initFile = initFile;
    }

    @Transactional
    @SneakyThrows
    @Override
    public void onApplicationEvent(@org.jetbrains.annotations.NotNull @NotNull ContextRefreshedEvent event) {
        if (enabled) {
            log.info("Running data initialization.");
            init();
            log.info("Data initialization finished.");
        } else {
            log.info("Skipping initialization.");
        }
    }

    protected void init() throws IOException {
        ExportTemplateDto[] templateDtos = readDtos();

        log.debug("Found {} templates.", templateDtos.length);

        processDtos(templateDtos);
    }

    protected void processDtos(ExportTemplateDto[] dtos) {
        Stream.of(dtos).forEach(dto -> {
            log.debug("Processing template {}.", dto.getId());

            ExportTemplate oldTemplate = repository.find(dto.getId());

            if (oldTemplate != null && !dto.isReplace()) {
                log.debug("Template exists. Skipping.");
                return;
            }

            cz.inqool.eas.common.storage.file.File storedFile = null;
            String dtoContentFile = dto.getContentFile();
            if (dtoContentFile != null) {

                File contentFile = new File(dtoContentFile);

                try (FileInputStream stream = new FileInputStream(contentFile);) {
                    if (oldTemplate != null) {
                        fileManager.remove(oldTemplate.getContent().getId());
                    }

                    storedFile = fileManager.store(contentFile.getName(), contentFile.length(), "text/xml", stream);
                } catch (IOException e) {
                    log.error("Report content file cannot be loaded. Skipping.", e);
                    return;
                }
            }

            ExportTemplate template = oldTemplate != null ? oldTemplate : new ExportTemplate();
            template.setId(dto.getId());
            template.setName(dto.getName());
            template.setActive(dto.isActive());
            template.setOrder(dto.getOrder());
            template.setLabel(dto.getLabel());
            template.setDataProvider(dto.getDataProvider());
            template.setDesignProvider(dto.getDesignProvider());
            template.setConfiguration(dto.getConfiguration());
            template.setTags(dto.getTags());
            template.setContent(storedFile);

            if (dto.getAllowedTypes() != null) {
                template.setAllowedTypes(dto.getAllowedTypes().stream().map(ExportType::valueOf).collect(Collectors.toSet()));
            }

            if (oldTemplate == null) {
                repository.create(template);
            } else {
                if (oldTemplate.getDeleted() != null) {
                    repository.getStore().restore(oldTemplate.getId());
                }
                repository.update(template);
            }
        });
    }

    protected ExportTemplateDto[] readDtos() {
        if (!initFile.exists()) {
            log.warn("Config file not found.");
            return new ExportTemplateDto[0];
        }

        String configString = ResourceReader.asString(initFile, Charset.defaultCharset());

        return JsonUtils.fromJsonString(configString, ExportTemplatesDto.class).getTemplates();
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired
    public void setRepository(ExportTemplateRepository repository) {
        this.repository = repository;
    }
}
