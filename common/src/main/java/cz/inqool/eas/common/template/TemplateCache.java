package cz.inqool.eas.common.template;

import com.google.common.hash.Hashing;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TemplateCache {
    private final Map<String, Template> cache = new ConcurrentHashMap<>();

    /**
     * Returns cached freemarker template for given string template. If there's no template cached, creates
     * and stores a new one.
     *
     * @param template             Template of which the freemarker template will be returned
     * @param configuration        freemarker template configuration used when creating a new template
     * @return cached template
     */
    public Template getOrCompute(@NotNull String template, Configuration configuration) {
        String fingerprint = computeFingerprint(template);

        return cache.computeIfAbsent(fingerprint, (f) -> {
            try {
                return new Template(fingerprint, template, configuration);
            } catch (IOException e) {
                throw new TemplateCompilationException("Error when creating template", e).debugInfo(template);
            }
        });
    }

    public void invalidateItem(@NotNull String templateString) {
        String fingerprint = computeFingerprint(templateString);
        cache.remove(fingerprint);
    }

    protected String computeFingerprint(String template) {
        return Hashing
                .sha256()
                .hashString(template, StandardCharsets.UTF_8)
                .toString();
    }
}
