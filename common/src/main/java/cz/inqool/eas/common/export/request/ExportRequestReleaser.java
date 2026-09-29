package cz.inqool.eas.common.export.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class ExportRequestReleaser {
    private ExportRequestService service;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    //@Scheduled(fixedRate = 1000)
    public void execute() {
        log.trace("Checking for forgotten export requests.");

        int forgotten = service.releaseForgotten();

        if (forgotten > 0) {
            log.debug("{} requests were released.", forgotten);
        } else {
            log.trace("No forgotten requests found.");
        }
    }

    @Autowired
    public void setService(ExportRequestService service) {
        this.service = service;
    }
}
