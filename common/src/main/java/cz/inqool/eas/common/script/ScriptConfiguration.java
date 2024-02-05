package cz.inqool.eas.common.script;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.script.ScriptEngine;
import java.util.function.Function;

/**
 * Configuration for script subsystem.
 *
 * If application wants to use script subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Slf4j
public abstract class ScriptConfiguration {
    /**
     * Constructs {@link ScriptExecutor} bean.
     */
    @Bean
    public ScriptExecutor scriptExecutor() {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        scriptExecutor.setScriptEngineFactory(scriptEngineFactory());
        return scriptExecutor;
    }

    protected abstract Function<ScriptType, ScriptEngine> scriptEngineFactory();
}
