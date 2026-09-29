package cz.inqool.eas.common.reporting.report;

import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.dictionary.index.DictionaryAutocomplete;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.common.reporting.dto.ReportDefinition;
import cz.inqool.eas.common.reporting.event.ReportGeneratedEvent;
import cz.inqool.eas.common.reporting.generator.DelegateReportGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

@Slf4j
public class ReportService {
    private DelegateReportGenerator generator;

    private ReportRepository repository;

    protected ApplicationEventPublisher eventPublisher;

    @Transactional
    public List<ReportDefinition> getAllowedDefinitions() {
        return generator.getAllowedDefinitions();
    }

    @Transactional
    public List<DictionaryAutocomplete> getDefinitionsAutocomplete() {
        return generator.getAllowedDefinitions()
                .stream()
                .map(definition -> new DictionaryAutocomplete(definition.getId(), definition.getLabel(), definition.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportDetail getReportByDefinition(String definitionId) {
        ReportDefinition definition = generator.getDefinition(definitionId);
        notNull(definition, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(definitionId).property("type", ReportDefinition.class.getSimpleName()))
                .debugInfo(info -> info.clazz(ReportDefinition.class))
                .logAll());

        if (definition.isAutogenerate()) {
            Report report = generator.generate(definitionId, Map.of());
            return ReportDetail.toView(report);
        }

        String userId = UserGenerator.getUser().getId();

        return repository.getLastByDefinition(definitionId, userId);
    }

    @Transactional
    public ReportDetail generate(String definitionId, Map<String, Object> configuration) {
        Report report = generator.generate(definitionId, configuration);

        Report generated = repository.create(report);

        eventPublisher.publishEvent(new ReportGeneratedEvent(this, generated));

        return repository.find(ReportDetail.class, report.getId());
    }

    @Autowired
    public void setRepository(ReportRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setGenerator(DelegateReportGenerator generator) {
        this.generator = generator;
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
