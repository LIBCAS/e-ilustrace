package cz.inqool.eas.common.test.simple;

import cz.inqool.eas.common.TestBase;
import cz.inqool.eas.common.dao.simple.keyvalue.SimpleKeyValueEntity;
import cz.inqool.eas.common.dao.simple.keyvalue.SimpleKeyValueRepository;
import cz.inqool.eas.common.tstutil.CommonTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple test for checking if {@link TestBase} is initialized and database is working...
 */
public class SimpleTest extends CommonTestBase {

    @Autowired
    private SimpleKeyValueRepository simpleKeyValueRepository;


    @Test
    @Transactional
    public void gotta_do_the_test_thing() {
        var entity = SimpleKeyValueEntity.builder()
                .key("qwe")
                .value("asd")
                .build();

        simpleKeyValueRepository.create(entity);

        SimpleKeyValueEntity found = simpleKeyValueRepository.find(entity.getId());

        assertThat(found.getId()).isEqualTo(entity.getId());
        assertThat(found.getKey()).isEqualTo(entity.getKey());
        assertThat(found.getValue()).isEqualTo(entity.getValue());
    }

}
