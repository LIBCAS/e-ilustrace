package cz.inqool.eas.common.reporting.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.inqool.eas.common.reporting.dto.ReportDefinition;
import cz.inqool.eas.common.reporting.report.Report;
import cz.inqool.eas.common.reporting.report.ReportColumn;
import cz.inqool.eas.common.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Slf4j
public abstract class ReportGenerator<INPUT, ITEM> {

    private final Class<INPUT> configurationClass;
    private ObjectMapper objectMapper;


    public ReportGenerator(Class<INPUT> configurationClass) {
        this.configurationClass = configurationClass;
    }


    @Transactional
    public Report generate(Map<String, Object> configuration) {
        String definitionId = getDefinition().getId();

        INPUT input = getObjectMapper().convertValue(configuration, configurationClass);

        GeneratorResult<ITEM> result = generateInternal(input);
        List<ReportColumn> columns = result.getColumns();
        List<ITEM> items = result.getItems();

        // @SuppressWarnings("unchecked")
        List data = items.stream()
                .map(item -> getObjectMapper().convertValue(item, Map.class))
                .collect(Collectors.toList());

        Report report = new Report();
        report.setDefinitionId(definitionId);
        report.setConfiguration(configuration);
        report.setData(data);
        report.setColumns(columns);

        return report;
    }

    protected boolean checkSupport(String definitionId) {
        return getDefinition().getId().equals(definitionId);
    }

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = JsonUtils.newObjectMapper();
            configureObjectMapper(objectMapper);
        }

        return objectMapper;
    }

    /**
     * Override in subclasses to configure object mapper
     */
    protected void configureObjectMapper(@NonNull ObjectMapper objectMapper) {
        objectMapper.disable(FAIL_ON_UNKNOWN_PROPERTIES);
    }

    protected abstract ReportDefinition getDefinition();

    protected abstract GeneratorResult<ITEM> generateInternal(INPUT input);


    @AllArgsConstructor
    @Data
    public static class GeneratorResult<ITEM> {
        List<ReportColumn> columns;
        List<ITEM> items;
    }
}
