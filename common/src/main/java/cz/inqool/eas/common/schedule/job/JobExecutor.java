package cz.inqool.eas.common.schedule.job;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cz.inqool.eas.common.schedule.job.JobExecutor.JobExecutionErrorDto.Fields;
import cz.inqool.eas.common.schedule.run.Run;
import cz.inqool.eas.common.schedule.run.RunRepository;
import cz.inqool.eas.common.schedule.run.RunState;
import cz.inqool.eas.common.script.ScriptExecutor;
import cz.inqool.eas.common.utils.JsonUtils;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.time.Instant;

@Slf4j
public class JobExecutor {
    private ScriptExecutor scriptExecutor;

    private RunRepository runRepository;

    private TransactionTemplate transactionTemplate;

    /**
     * Executes provided job.
     *
     * @param job time-based job that should be executed.
     */
    public void executeJob(Job job) {
        Run run = startExecution(job);

        ByteArrayOutputStream console = new ByteArrayOutputStream();

        RunState state = RunState.FINISHED;
        Object result = null;
        try {
            result = scriptExecutor.executeScript(
                    job.getScriptType(),
                    job.getScript(),
                    job.isUseTransaction(),
                    null,
                    console
            );
        } catch (Exception e) {
            log.error("Error during execution of Job '" + job.getName() + "' (" + job.getId() + ")", e);
            state = RunState.ERROR;
            result = JobExecutionErrorDto.of(e);
        } finally {
            endExecution(run, state, console.toString(Charset.defaultCharset()), result);
        }
    }

    /**
     * Starts the job execution by creating a run object for provided job.
     *
     * @param job time-based job
     * @return run object for the execution
     */
    protected Run startExecution(Job job) {
        Run run = new Run();
        run.setJob(job);
        run.setState(RunState.STARTED);
        run.setStartTime(Instant.now());

        if (job.logAction == LogAction.ALWAYS) {
            return transactionTemplate.execute((status) -> runRepository.create(run));
        } else {
            return run;
        }
    }

    /**
     * Finishes the job execution by saving execution's data to the run object.
     *
     * @param run     run object for the execution
     * @param state   state of the run
     * @param console job execution's console log
     * @param result  object created by the job execution, can be null
     */
    protected void endExecution(Run run, RunState state, String console, Object result) {
        run.setState(state);
        run.setConsole(console);
        run.setEndTime(Instant.now());
        if (result != null) {
            run.setResult(JsonUtils.toJsonString(result, true));
        }

        if (run.getJob().getLogAction() == LogAction.ALWAYS) {
            transactionTemplate.executeWithoutResult((status) -> runRepository.update(run));
        } else if (run.getJob().getLogAction() == LogAction.ON_ERROR && state == RunState.ERROR) {
            transactionTemplate.executeWithoutResult((status) -> runRepository.create(run));
        }
    }

    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Autowired
    public void setScriptExecutor(ScriptExecutor scriptExecutor) {
        this.scriptExecutor = scriptExecutor;
    }

    @Autowired
    public void setRunRepository(RunRepository runRepository) {
        this.runRepository = runRepository;
    }


    @Getter
    @JsonPropertyOrder({Fields.name, Fields.message, Fields.cause})
    @FieldNameConstants
    public static class JobExecutionErrorDto {

        private final String name;
        private final String message;
        private final JobExecutionErrorDto cause;


        public JobExecutionErrorDto(String name, String message, JobExecutionErrorDto cause) {
            this.name = name;
            this.message = message;
            this.cause = cause;
        }

        public static JobExecutionErrorDto of(Throwable e) {
            if (e == null) {
                return null;
            }

            return new JobExecutionErrorDto(
                    e.getClass().getName(),
                    e.getMessage(),
                    of(e.getCause())
            );
        }
    }
}
