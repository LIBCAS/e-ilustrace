package cz.inqool.eas.common.tstutil;

import cz.inqool.eas.common.TestBase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(classes = TestInitializer.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
public class CommonTestBase extends TestBase {

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TestBase::getPostgresJdbcUrl);
        registry.add("spring.datasource.username", TestBase::getPostgresUsername);
        registry.add("spring.datasource.password", TestBase::getPostgresPassword);
        registry.add("spring.datasource.driver", TestBase::getPostgresDriverClassName);

        registry.add("spring.redis.host", TestBase::getRedisContainerIpAddress);
        registry.add("spring.redis.port", TestBase::getRedisPort);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");

        registry.add("spring.jpa.hibernate.naming.implicit-strategy", () -> "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        registry.add("spring.jpa.hibernate.naming.physical-strategy", () -> "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");

        registry.add("spring.liquibase.changeLog", () -> "classpath:/changelog/dbchangelog-test.eas.xml");
        registry.add("spring.liquibase.drop-first", () -> "true");
    }

}
