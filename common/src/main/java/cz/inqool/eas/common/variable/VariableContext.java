package cz.inqool.eas.common.variable;

import cz.inqool.eas.common.exception.GeneralException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

/**
 * Utility service for passing variables deep into call hierarchy without the need to pass it as parameters.
 *
 * Works only in sync code.
 */
@Service
public class VariableContext {
    private final ThreadLocal<Map<String, Object>> contextHolder = new ThreadLocal<>();

    public void doInContext(Runnable runnable) {
        initContext();

        try {
            runnable.run();
        } finally {
            closeContext();
        }
    }

    public <T> T doInContextWithResult(Supplier<T> runnable) {
        initContext();

        try {
            return runnable.get();
        } finally {
            closeContext();
        }
    }

    public boolean isInContext() {
        Map<String, Object> context = contextHolder.get();
        return context != null;
    }

    public void initContext() {
        contextHolder.set(new HashMap<>());
    }

    public void closeContext() {
        contextHolder.set(null);
    }

    public <T> void putVariable(String name, T variable) {
        Map<String, Object> context = getContext();
        context.put(name, variable);
    }

    public <T> T getVariable(String name) {
        Map<String, Object> context = getContext();
        return (T) context.get(name);
    }

    protected Map<String, Object> getContext() {
        Map<String, Object> context = contextHolder.get();
        notNull(context, () -> new GeneralException("No variable context present"));
        return context;
    }
}
