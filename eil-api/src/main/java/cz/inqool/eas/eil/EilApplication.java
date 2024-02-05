package cz.inqool.eas.eil;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@OpenAPIDefinition(info = @Info(title = "EIL backend", description = "Main EIL backend service", version = "1"))
@SpringBootApplication(exclude = {WebServicesAutoConfiguration.class})
@ComponentScan("cz.inqool")
@ConfigurationPropertiesScan("cz.inqool")
@EntityScan("cz.inqool")
public class EilApplication {

    public static void main(String[] args) {
        SpringApplication.run(EilApplication.class, args);
    }

}