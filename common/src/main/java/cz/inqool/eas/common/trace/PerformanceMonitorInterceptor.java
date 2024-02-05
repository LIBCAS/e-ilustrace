package cz.inqool.eas.common.trace;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;
import org.springframework.util.StopWatch;

/**
 * Copy of {@link org.springframework.aop.interceptor.PerformanceMonitorInterceptor} with
 * added configurability.
 */
@SuppressWarnings("serial")
public class PerformanceMonitorInterceptor extends AbstractMonitoringInterceptor {
    /**
     * Use milliseconds instead of nanoseconds.
     */
    boolean useMs;

    /**
     * Use threshold (in nanoseconds) to not write very short time durations.
     */
    Long thresholdNanos;

    /**
     * Create a new PerformanceMonitorInterceptor with a static logger.
     */
    public PerformanceMonitorInterceptor(boolean useMs) {
        this.useMs = useMs;
    }

    /**
     * Create a new PerformanceMonitorInterceptor with a dynamic or static logger,
     * according to the given flag.
     * @param useDynamicLogger whether to use a dynamic logger or a static logger
     * @see #setUseDynamicLogger
     */
    public PerformanceMonitorInterceptor(boolean useDynamicLogger, boolean useMs) {
        setUseDynamicLogger(useDynamicLogger);
        this.useMs = useMs;
    }

    /**
     * Create a new PerformanceMonitorInterceptor with a dynamic or static logger,
     * according to the given flag.
     * @param useDynamicLogger whether to use a dynamic logger or a static logger
     * @see #setUseDynamicLogger
     */
    public PerformanceMonitorInterceptor(boolean useDynamicLogger, boolean useMs, Long thresholdNanos) {
        setUseDynamicLogger(useDynamicLogger);
        this.useMs = useMs;
        this.thresholdNanos = thresholdNanos;
    }


    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String name = createInvocationTraceName(invocation);
        StopWatch stopWatch = new StopWatch(name);
        stopWatch.start(name);
        try {
            return invocation.proceed();
        }
        finally {
            stopWatch.stop();

            long totalTimeNanos = stopWatch.getTotalTimeNanos();

            if (thresholdNanos == null || totalTimeNanos > thresholdNanos) {
                if (useMs) {
                    writeToLog(logger, "StopWatch '" + stopWatch.getId() + "': running time = " + totalTimeNanos /1e6 + " ms");
                } else {
                    writeToLog(logger, "StopWatch '" + stopWatch.getId() + "': running time = " + totalTimeNanos + " ns");
                }
            }
        }
    }
}
