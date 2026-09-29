package cz.inqool.eas.common.tstutil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Test initializer class for spring boot testing
 */
@SpringBootApplication(scanBasePackages = "cz.inqool.eas.common")
@EntityScan(basePackages = "cz.inqool.eas.common")
public class TestInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TestInitializer.class, args);
    }
}
