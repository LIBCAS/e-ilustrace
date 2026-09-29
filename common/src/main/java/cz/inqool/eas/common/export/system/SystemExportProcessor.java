package cz.inqool.eas.common.export.system;

import cz.inqool.eas.common.client.export.runner.ExportRunnerClient;
import cz.inqool.eas.common.export.request.ExportRequest;
import cz.inqool.eas.common.export.request.ExportRequestService;
import cz.inqool.eas.common.export.template.ExportTemplate;
import cz.inqool.eas.common.export.template.ExportTemplateRepository;
import cz.inqool.eas.common.export.template.ExportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class SystemExportProcessor {
    private ExportRequestService service;

    private ExportTemplateRepository repository;

    private ExportRunnerClient runnerClient;

    private PlatformTransactionManager transactionManager;

    /**
     * Synchronously create system export request and process it through export runner.
     * @param templateId Id of the template
     * @param type type of the result
     * @param configuration configuration
     * @return processed Export request
     */
    public ExportRequest createAndProcess(String templateId, ExportType type, String configuration) {
        ExportTemplate template = repository.getRef(templateId);

        ExportRequest request = new ExportRequest();
        request.setTemplate(template);
        request.setType(type);
        request.setPriority(0);
        request.setSystemRequest(true);
        request.setConfiguration(configuration);

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        transactionTemplate.executeWithoutResult(status -> service.createInternal(request));
        runnerClient.process(request.getId());

        return service.getInternal(ExportRequest.class, request.getId());
    }

    @Autowired
    public void setService(ExportRequestService service) {
        this.service = service;
    }

    @Autowired
    public void setRepository(ExportTemplateRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setRunnerClient(ExportRunnerClient runnerClient) {
        this.runnerClient = runnerClient;
    }

    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
