package cz.inqool.eas.common.template;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class TemplateService {
    private TemplateCache cache;

    private Configuration configuration;

    public String process(String template, Map<String, Object> data) {
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(cache.getOrCompute(template, configuration), data);
        } catch (IOException | TemplateException e) {
            throw new TemplateProcessingException("Error when processing template", e).debugInfo(template);
        }
    }

    @Autowired
    public void setCache(TemplateCache cache) {
        this.cache = cache;
    }

    @Autowired
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
