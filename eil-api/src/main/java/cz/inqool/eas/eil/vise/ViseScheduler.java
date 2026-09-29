package cz.inqool.eas.eil.vise;

import cz.inqool.eas.common.domain.index.reindex.ReindexService;
import cz.inqool.eas.eil.mirador.MiradorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static cz.inqool.eas.eil.vise.ViseUtils.RECORD_REPOSITORY;

@Service
@Slf4j
public class ViseScheduler {
    private MiradorService miradorService;
    private ViseManager viseManager;
    private ReindexService reindexService;

//    @Scheduled(cron = "${eil.cron.vise.restart}")
    public void viseProjectRestart() {
        log.info("Restarting Vise projects.");

        try {
            log.debug("Restarting 'Illustrations' Vise project STARTED.");
            viseManager.resetViseImages();
            log.debug("Restarting 'Illustrations' Vise project FINISHED.");
        } catch (Exception e) {
            log.debug("Exception occurred during Vise reindex.", e);
        }

        try {
            log.debug("Restarting 'Cantaloupe' Vise project STARTED.");
            miradorService.resetMiradorImages();
            log.debug("Restarting 'Cantaloupe' Vise project FINISHED.");
        } catch (Exception e) {
            log.debug("Exception occurred during Cantaloupe reindex.", e);
        } finally {
            reindexService.reindex(List.of(RECORD_REPOSITORY));

        }
    }

    @Autowired
    public void setMiradorService(MiradorService miradorService) {
        this.miradorService = miradorService;
    }

    @Autowired
    public void setViseManager(ViseManager viseManager) {
        this.viseManager = viseManager;
    }

    @Autowired
    public void setReindexService(ReindexService reindexService) {
        this.reindexService = reindexService;
    }
}
