package cz.inqool.eas.common.script;

import cz.inqool.eas.common.action.ActionException;
import cz.inqool.eas.common.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Precompiled script function.
 */
@Slf4j
public class ScriptFunction {
    private final Invocable invocable;
    private final String name;
    private final TransactionTemplate template;

    public ScriptFunction(ScriptEngine engine, String name, TransactionTemplate template) {
        this.invocable = (Invocable) engine;
        this.name = name;
        this.template = template;
    }

    public Object call(Object... args) {
        try {
            if (template != null) {
                return template.execute((status) -> {
                    try {
                        return invocable.invokeFunction(name, args);
                    } catch (ScriptException | NoSuchMethodException e) {
                        throw new GeneralException(e);
                    }
                });
            } else {
                return invocable.invokeFunction(name, args);
            }
        } catch (Exception e) {
            log.error("Failed to execute Script function {}.", name);
            throw new ActionException(e);
        }
    }
}
