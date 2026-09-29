package cz.inqool.eas.common.script;

import cz.inqool.eas.common.exception.GeneralException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.script.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static cz.inqool.eas.common.utils.AssertionUtils.ifPresent;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

@Slf4j
public class ScriptExecutor {
    private ApplicationContext context;

    private PlatformTransactionManager transactionManager;

    @Setter
    private Function<ScriptType, ScriptEngine> scriptEngineFactory;

    /**
     * Executes provided script with a script engine.
     *
     * @param scriptType     script's language
     * @param script         script's source code
     * @param useTransaction true if script should be executed in transaction mode
     * @param configuration  parameters for the script
     * @param console        open output data stream for streaming console logs
     * @return object created by the script
     */
    public Object executeScript(ScriptType scriptType, String script, boolean useTransaction, Map<String, Object> configuration, OutputStream console) {
        ScriptEngine scriptEngine = scriptEngineFactory.apply(scriptType);
        notNull(scriptEngine, () -> new UnsupportedOperationException("Unsupported script engine '" + scriptType + "'"));

        injectContextAndConfiguration(scriptEngine, configuration);

        if (useTransaction) {
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            return template.execute((status) -> executeInternal(scriptEngine, script, console));

        } else {
            return executeInternal(scriptEngine, script, console);
        }
    }

    private Object executeInternal(ScriptEngine engine, String script, OutputStream console) {
        OutputStreamWriter writer = null;
        if (console != null) {
            writer = new OutputStreamWriter(console);
            engine.getContext().setWriter(writer);
        }

        try {
            return engine.eval(script);
        } catch (ScriptException ex) {
            throw new GeneralException(ex);
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                }
            } catch (IOException e) {
                log.error("Failed to flush", e);
            }
        }
    }

    public ScriptFunction prepareFunction(ScriptType scriptType, String script, String functionName, boolean useTransaction, Map<String, Object> configuration, OutputStream console) {
        ScriptEngine scriptEngine = scriptEngineFactory.apply(scriptType);
        notNull(scriptEngine, () -> new UnsupportedOperationException("Unsupported script engine '" + scriptType + "'"));

        injectContextAndConfiguration(scriptEngine, configuration);

        prepareFunctionInternal(scriptEngine, script, console);

        TransactionTemplate template = null;
        if (useTransaction) {
            template = new TransactionTemplate(transactionManager);
        }

        return new ScriptFunction(scriptEngine, functionName, template);
    }

    private void prepareFunctionInternal(ScriptEngine engine, String script, OutputStream console) {
        OutputStreamWriter writer = null;
        if (console != null) {
            writer = new OutputStreamWriter(console);
            engine.getContext().setWriter(writer);
        }

        try {
            engine.eval(script);
        } catch (ScriptException ex) {
            throw new GeneralException(ex);
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                }
            } catch (IOException e) {
                log.error("Failed to flush", e);
            }
        }
    }

    public List<ScriptType> getSupportedScriptTypes() {
        List<ScriptType> supportedTypes = new ArrayList<>();

        for (ScriptType type : ScriptType.values()) {
            try {
                scriptEngineFactory.apply(type);
                supportedTypes.add(type);
            } catch (Exception e) {
                // silently ignore
            }
        }

        return supportedTypes;
    }

    protected void injectContextAndConfiguration(ScriptEngine engine, Map<String, Object> configuration) {
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        ifPresent(configuration, bindings::putAll);
        bindings.put("spring", context);
        bindings.put("log", log);
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
