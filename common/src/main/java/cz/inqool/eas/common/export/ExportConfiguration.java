package cz.inqool.eas.common.export;

import cz.inqool.eas.common.exception.GeneralException;
import cz.inqool.eas.common.export.access.ExportAccessChecker;
import cz.inqool.eas.common.export.batch.ExportBatchRepository;
import cz.inqool.eas.common.export.batch.ExportBatchService;
import cz.inqool.eas.common.export.init.ExportTemplateInitializer;
import cz.inqool.eas.common.export.request.ExportRequestApi;
import cz.inqool.eas.common.export.request.ExportRequestReleaser;
import cz.inqool.eas.common.export.request.ExportRequestRepository;
import cz.inqool.eas.common.export.request.ExportRequestService;
import cz.inqool.eas.common.export.system.SystemExportProcessor;
import cz.inqool.eas.common.export.template.ExportTemplateApi;
import cz.inqool.eas.common.export.template.ExportTemplateRepository;
import cz.inqool.eas.common.export.template.ExportTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class ExportConfiguration {
    @Autowired
    private ConfigurableEnvironment env;

    /**
     * Adds custom property source to spring for setting the url path of
     * {@link ExportTemplateApi} and {@link ExportRequestApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for export_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("export.url", exportUrl());

        MapPropertySource propertySource = new MapPropertySource("export_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    protected boolean isInitEnabled() {
        return false;
    }

    protected Resource getInitFile() {
        throw new GeneralException("You need to define init file for export module.");
    };

    /**
     * Constructs {@link ExportTemplateRepository} bean.
     */
    @Bean
    public ExportTemplateRepository exportTemplateRepository() {
        return new ExportTemplateRepository();
    }

    /**
     * Constructs {@link ExportTemplateService} bean.
     */
    @Bean
    public ExportTemplateService exportTemplateService() {
        return new ExportTemplateService();
    }

    /**
     * Constructs {@link ExportTemplateApi} bean.
     */
    @Bean
    public ExportTemplateApi exportTemplateApi() {
        return new ExportTemplateApi();
    }

    /**
     * Constructs {@link ExportTemplateInitializer} bean.
     */
    @Bean
    public ExportTemplateInitializer exportTemplateInitializer() {
        boolean initEnabled = isInitEnabled();
        Resource initFile = initEnabled ? getInitFile() : null;

        return new ExportTemplateInitializer(initEnabled, initFile);
    }

    /**
     * Constructs {@link ExportRequestRepository} bean.
     */
    @Bean
    public ExportRequestRepository exportRequestRepository() {
        return new ExportRequestRepository();
    }

    /**
     * Constructs {@link ExportRequestService} bean.
     */
    @Bean
    public ExportRequestService exportRequestService() {
        return new ExportRequestService();
    }

    /**
     * Constructs {@link ExportRequestApi} bean.
     */
    @Bean
    public ExportRequestApi exportRequestApi() {
        return new ExportRequestApi();
    }

    /**
     * Constructs {@link ExportRequestReleaser} bean.
     */
    @Bean
    public ExportRequestReleaser exportRequestReleaser() {
        return new ExportRequestReleaser();
    }


    /**
     * Constructs {@link ExportBatchRepository} bean.
     */
    @Bean
    public ExportBatchRepository exportBatchRepository() {
        return new ExportBatchRepository();
    }

    /**
     * Constructs {@link ExportBatchService} bean.
     */
    @Bean
    public ExportBatchService exportBatchService() {
        return new ExportBatchService();
    }

    /**
     * System export processor is accessible only if Runner client is defined.
     */
    @Bean
    public SystemExportProcessor systemExportProcessor() {
        return new SystemExportProcessor();
    }

    /**
     * Returns url path of {@link ExportTemplateApi} and {@link ExportRequestApi}.
     */
    protected abstract String exportUrl();

    /**
     * Constructs custom access checker bean.
     */
    public abstract ExportAccessChecker exportAccessChecker();
}
