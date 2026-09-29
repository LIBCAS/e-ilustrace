package cz.inqool.eas.common.reporting.generator;

import cz.inqool.eas.common.exception.v2.ForbiddenObject;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.common.reporting.access.ReportAccessChecker;
import cz.inqool.eas.common.reporting.dto.ReportDefinition;
import cz.inqool.eas.common.reporting.exception.ReportGeneratorMissingException;
import cz.inqool.eas.common.reporting.report.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.exception.v2.ExceptionCode.OBJECT_ACCESS_DENIED;
import static cz.inqool.eas.common.utils.AssertionUtils.isTrue;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;
import static java.util.Comparator.comparing;

@Slf4j
public class DelegateReportGenerator {
    private ReportAccessChecker accessChecker;

    private List<ReportGenerator<?, ?>> generators = new ArrayList<>();

    public Report generate(String definitionId, Map<String, Object> configuration) {
        ReportDefinition definition = getDefinition(definitionId);
        notNull(definition, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(definitionId).property("type", ReportDefinition.class.getSimpleName()))
                .debugInfo(info -> info.clazz(ReportDefinition.class))
                .logAll());
        isTrue(accessChecker.checkAccess(definition), () -> new ForbiddenObject(OBJECT_ACCESS_DENIED)
                .details(details -> details.id(definitionId).property("type", ReportDefinition.class.getSimpleName()))
                .debugInfo(info -> info.clazz(ReportDefinition.class)));

        ReportGenerator<?, ?> generator = generators
                .stream()
                .filter(g -> g.checkSupport(definitionId))
                .findFirst()
                .orElse(null);

        if (generator == null) {
            log.error("Failed to find relevant generator for report.");
            throw new ReportGeneratorMissingException("Failed to find relevant generator for report.");
        }

        log.trace("Start generating report.");
        Report report = generator.generate(configuration);
        log.trace("Finished generating report.");

        return report;
    }

    public ReportDefinition getDefinition(String id) {
        return getDefinitions()
                .stream().filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<ReportDefinition> getDefinitions() {
        return generators
                .stream()
                .map(ReportGenerator::getDefinition)
                .sorted(comparing(ReportDefinition::getOrder))
                .collect(Collectors.toList());
    }

    public List<ReportDefinition> getAllowedDefinitions() {
        List<ReportDefinition> definitions = getDefinitions();

        Map<String, Boolean> accessMap = accessChecker.checkAccess(definitions);

        return definitions
                .stream()
                .filter(definition -> accessMap.get(definition.getId()))
                .collect(Collectors.toList());
    }

    @Autowired
    public void setAccessChecker(ReportAccessChecker accessChecker) {
        this.accessChecker = accessChecker;
    }

    @Autowired(required = false)
    public void setGenerators(List<ReportGenerator<?, ?>> generators) {
        this.generators = generators;
    }
}
