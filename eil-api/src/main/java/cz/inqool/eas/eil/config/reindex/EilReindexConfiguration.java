package cz.inqool.eas.eil.config.reindex;

import cz.inqool.eas.common.domain.index.reindex.ReindexConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EilReindexConfiguration extends ReindexConfiguration {
    @Override
    protected String reindexUrl() {
        return "/reindex";
    }
}
