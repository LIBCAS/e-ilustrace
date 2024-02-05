package cz.inqool.eas.eil.config.mapping;

import cz.inqool.eas.common.domain.index.mapping.IndexMappingConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EilIndexMappingConfiguration extends IndexMappingConfiguration {
    @Override
    protected String indexMappingUrl() {
        return "/index-mapping";
    }
}
