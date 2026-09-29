package cz.inqool.eas.common;

import cz.inqool.eas.common.domain.DomainRepository;
import cz.inqool.eas.common.domain.index.DomainIndexedObject;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.Base58;

import java.time.Duration;
import java.util.Set;

abstract public class TestBase {

    private static final String POSTGRE_SQL_IMAGE   = "postgres:11-alpine";
    private static final String ELASTICSEARCH_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:7.17.4";
    private static final String REDIS_IMAGE         = "redis:6.0-alpine";


    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(POSTGRE_SQL_IMAGE)
            .withDatabaseName("test-eas-common-db")
            .withUsername("eas")
            .withPassword("pass123")
            .withReuse(true)
            .withCreateContainerCmdModifier(createContainerCmd -> createContainerCmd.withName("postgresql_test_container"));

    private static final GenericContainer<?> ELASTICSEARCH_CONTAINER = new GenericContainer<>(new ImageFromDockerfile()
            .withDockerfileFromBuilder(
                    builder -> builder
                            .from(ELASTICSEARCH_IMAGE)
                            .run("bin/elasticsearch-plugin", "install", "analysis-icu")
                            .build()
            ))
            .withNetworkAliases("elasticsearch-" + Base58.randomString(6))
            .withEnv("discovery.type", "single-node")
            .withExposedPorts(9200, 9300)
            .waitingFor(new HttpWaitStrategy()
                    .forPort(9200)
                    .withStartupTimeout(Duration.ofMinutes(2)))
            .withReuse(true)
            .withCreateContainerCmdModifier(createContainerCmd -> createContainerCmd.withName("elasticsearch_test_container"));

    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(new ImageFromDockerfile()
            .withDockerfileFromBuilder(
                    builder -> builder
                            .from(REDIS_IMAGE)
                            .build()
            ))
            .withExposedPorts(6379)
            .withReuse(true)
            .withCreateContainerCmdModifier(createContainerCmd -> createContainerCmd.withName("redis_test_container"));

    static {
        POSTGRE_SQL_CONTAINER.start();
        ELASTICSEARCH_CONTAINER.start();
        REDIS_CONTAINER.start();
    }

    @Autowired
    @Lazy
    private Set<DomainRepository<?, ?, ?, ?, ?>> repositories;


    protected Set<Class<? extends DomainIndexedObject<?, ?>>> getIndexedObjectClasses() {
        return Set.of();
    }

    @BeforeEach
    protected void dropAndCreateElasticIndexed() {
        repositories.stream()
                .filter(repo -> getIndexedObjectClasses().contains(repo.getIndexableType()))
                .forEach(repo -> {
                    if (repo.isIndexInitialized()) {
                        repo.dropIndex();
                    }
                    repo.initIndex();
                });
    }

    public static Integer getElasticsearchPort() {
        return ELASTICSEARCH_CONTAINER.getMappedPort(9200);
    }
    public static String getPostgresJdbcUrl() {
        return POSTGRE_SQL_CONTAINER.getJdbcUrl();
    }
    public static String getPostgresUsername() {
        return POSTGRE_SQL_CONTAINER.getUsername();
    }
    public static String getPostgresPassword() {
        return POSTGRE_SQL_CONTAINER.getPassword();
    }
    public static String getPostgresDriverClassName() {
        return POSTGRE_SQL_CONTAINER.getDriverClassName();
    }
    public static String getRedisContainerIpAddress() {
        return REDIS_CONTAINER.getContainerIpAddress();
    }
    public static String getRedisPort() {
        return REDIS_CONTAINER.getMappedPort(6379).toString();
    }
}
