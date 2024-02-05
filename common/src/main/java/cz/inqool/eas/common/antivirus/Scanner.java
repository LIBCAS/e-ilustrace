package cz.inqool.eas.common.antivirus;

import cz.inqool.eas.common.antivirus.clamav.ClamAV;
import cz.inqool.eas.common.antivirus.scan.Scan;
import cz.inqool.eas.common.antivirus.scan.ScanResult;
import cz.inqool.eas.common.antivirus.scan.ScanStore;
import cz.inqool.eas.common.storage.file.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.io.InputStream;

/**
 * Service for scanning files with Clam AntiVirus.
 */
@Slf4j
public class Scanner {
    private ClamAV clamAV;

    private ScanStore store;

    /**
     * Scans data stream with antivirus and stores a scan result.
     *
     * @param file       File reference
     * @param dataStream File's data stream
     * @return Result of the scan.
     */
    @Transactional
    public ScanResult scanFile(File file, InputStream dataStream) {
        log.debug("Scanning {}.", file);
        ScanResult result;

        try {
            boolean virusFound = clamAV.scanFile(dataStream);
            result = virusFound ? ScanResult.VIRUS_FOUND : ScanResult.OK;
        } catch (Exception e) {
            log.warn("File can not be scanned.", e);
            result = ScanResult.ERROR;
        }

        Scan scan = new Scan();
        scan.setContent(file);
        scan.setResult(result);
        store.create(scan);

        log.debug("\t Scan result: {}", result);

        return result;
    }

    @Autowired
    public void setClamAV(ClamAV clamAV) {
        this.clamAV = clamAV;
    }

    @Autowired
    public void setStore(ScanStore store) {
        this.store = store;
    }
}
