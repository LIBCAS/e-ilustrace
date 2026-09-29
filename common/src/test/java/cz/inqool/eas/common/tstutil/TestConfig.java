package cz.inqool.eas.common.tstutil;

import cz.inqool.eas.common.TestBase;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@TestConfiguration
//@EnableTransactionManagement
public class TestConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        ClientConfiguration configuration = ClientConfiguration.builder()
                .connectedTo("localhost:" + TestBase.getElasticsearchPort())
                .build();
        return RestClients.create(configuration).rest();
    }
}
