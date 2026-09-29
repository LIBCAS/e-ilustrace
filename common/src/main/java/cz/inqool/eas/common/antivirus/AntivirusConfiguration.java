package cz.inqool.eas.common.antivirus;

import cz.inqool.eas.common.antivirus.clamav.ClamAV;
import cz.inqool.eas.common.antivirus.scan.ScanStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for scan subsystem.
 *
 * If application wants to use scan subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Slf4j
public abstract class AntivirusConfiguration {
    /**
     * Constructs {@link ScanStore} bean.
     */
    @Bean
    public ScanStore scanStore() {
        return new ScanStore();
    }

    /**
     * Constructs {@link Scanner} bean.
     */
    @Bean
    public Scanner scanner() {
        return new Scanner();
    }

    /**
     * Constructs {@link ClamAV} bean.
     */
    @Bean
    public ClamAV clamAV() {
        ClamAV clamAV = new ClamAV();
        clamAV.setServerHostname(getServerHostname());
        clamAV.setServerPort(getServerPort());
        clamAV.setPlatform(getPlatform());
        return clamAV;
    }

    /**
     * Hostname of server where ClamAV daemon runs
     */
    protected abstract String getServerHostname();

    /**
     * Port of server where ClamAV daemon runs
     */
    protected abstract int getServerPort();

    /**
     * Platform of server where ClamAV daemon runs
     * UNIX,
     * WINDOWS,
     * JVM_PLATFORM
     */
    protected abstract String getPlatform();
}
