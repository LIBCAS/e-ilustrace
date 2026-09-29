package cz.inqool.eas.common.pdfa.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for PDFA converter subsystem.
 *
 * If application wants to use PDFA converter subsystem,
 * it needs to extend this class and add {@link Configuration} annotation.
 */
@Slf4j
public abstract class MockPdfaConfiguration {

    /**
     * Constructs {@link MockPdfaConverter} bean.
     */
    @Bean
    public MockPdfaConverter pdfaConverter() {
        MockPdfaConverter mockedConverter = new MockPdfaConverter();
        mockedConverter.setFileNamePrefix(fileNamePrefix());
        return mockedConverter;
    }

    /**
     * File name prefix to help distinguish files created by Mock Converter.
     *
     * @return string that will be prefixed to a name of file created by mock converter.
     */
    protected String fileNamePrefix() {
        return "";
    }
}
