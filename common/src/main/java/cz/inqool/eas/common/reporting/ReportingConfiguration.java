package cz.inqool.eas.common.reporting;

import cz.inqool.eas.common.reporting.access.ReportAccessChecker;
import cz.inqool.eas.common.reporting.generator.DelegateReportGenerator;
import cz.inqool.eas.common.reporting.report.ReportApi;
import cz.inqool.eas.common.reporting.report.ReportRepository;
import cz.inqool.eas.common.reporting.report.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for reporting subsystem.
 *
 * If application wants to use reporting subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 *
 */
@Slf4j
public abstract class ReportingConfiguration {
    @Autowired
    private ConfigurableEnvironment env;


    /**
     * Adds custom property source to spring for setting the url path of {@link ReportApi}.
     */
    @PostConstruct
    public void registerPropertySource() {
        log.debug("Generating property source for reporting_configuration");

        Map<String, Object> properties = new HashMap<>();
        properties.put("reporting.url", reportingUrl());

        MapPropertySource propertySource = new MapPropertySource("reporting_configuration", properties);
        env.getPropertySources().addLast(propertySource);
    }

    /**
     * Constructs {@link ReportRepository} bean.
     */
    @Bean
    public ReportRepository reportRepository() {
        return new ReportRepository();
    }

    /**
     * Constructs {@link DelegateReportGenerator} bean.
     */
    @Bean
    public DelegateReportGenerator delegateReportGenerator() {
        return new DelegateReportGenerator();
    }

    /**
     * Constructs {@link ReportService} bean.
     */
    @Bean
    public ReportService reportService() {
        return new ReportService();
    }

    /**
     * Constructs {@link ReportApi} bean.
     */
    @Bean
    public ReportApi reportApi() {
        return new ReportApi();
    }

    /**
     * Returns url path of {@link ReportApi}.
     */
    protected abstract String reportingUrl();

    /**
     * Constructs custom access checker bean.
     */
    public abstract ReportAccessChecker reportAccessChecker();
}
