package cz.inqool.eas.common.antivirus.clamav;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.ClamavException;
import xyz.capybara.clamav.Platform;

import java.io.InputStream;

/**
 * Component for delegating scanning to Clam AntiVirus.
 */
@Slf4j
@Getter
@Setter
public class ClamAV {
    /**
     * Hostname of server where ClamAV daemon runs
     */
    private String serverHostname;

    /**
     * Port of server where ClamAV daemon runs
     */
    private int serverPort;

    /**
     * Platform of server where ClamAV daemon runs
     * UNIX,
     * WINDOWS,
     * JVM_PLATFORM
     */
    private String platform;

    public boolean scanFile(InputStream file) {
        ClamavClient client = new ClamavClient(serverHostname, serverPort, Platform.valueOf(platform));
        xyz.capybara.clamav.commands.scan.result.ScanResult scanResult = client.scan(file);

        if (scanResult instanceof xyz.capybara.clamav.commands.scan.result.ScanResult.OK) {
            return false;
        } else if (scanResult instanceof xyz.capybara.clamav.commands.scan.result.ScanResult.VirusFound) {
            return true;
        } else {
            log.warn("Invalid scan result '{}'.", scanResult);
            return false;
        }
    }

    public boolean testConnection() {
        try {
            ClamavClient client = new ClamavClient(serverHostname, serverPort, Platform.valueOf(platform));
            client.ping();
            return true;
        } catch (ClamavException ex) {
            log.error("Antivirus error", ex);
            return false;
        }
    }
}
