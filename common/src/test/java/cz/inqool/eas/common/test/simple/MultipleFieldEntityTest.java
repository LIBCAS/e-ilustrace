package cz.inqool.eas.common.test.simple;

import cz.inqool.eas.common.dao.simple.keyvalue.SimpleKeyValueEntity;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsEntity;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsEntity.Coordinates;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsRepository;
import cz.inqool.eas.common.tstutil.CommonTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for testing {@link MultipleFieldsEntity} and its repositories
 *
 * @author : olda
 * @since : 05/10/2020, Mon
 **/
@Transactional
public class MultipleFieldEntityTest extends CommonTestBase {

    @Autowired
    private MultipleFieldsRepository multipleFieldsRepository;

    @Test
    public void create_find_empty() {
        var entity = new MultipleFieldsEntity();
        multipleFieldsRepository.create(entity);

        var found = multipleFieldsRepository.find(entity.getId());
        assertThat(found).isEqualTo(entity);
        assertThat(found.getId()).isEqualTo(entity.getId());
    }

    @Test
    public void create_find_full() {
        var entity = new MultipleFieldsEntity();
        entity.setShortString("short string");
        entity.setLongString("long string asdasdadasdsadasdsaqweqwewqsfdvvjd woifubifjbepmgf wrgweoupw iu");
        entity.setBlobText("blob text qweqeqw .....");
        entity.setBooleanObject(true);
        entity.setBooleanPrimitive(true);
        entity.setCharObject('j');
        entity.setCharPrimitive('f');
        entity.setCharArray("char array".toCharArray());
        entity.setByteObject((byte) 10);
        entity.setBytePrimitive((byte) 1);
        entity.setByteArray("byte array".getBytes());
        entity.setIntegerObject(5);
        entity.setIntegerPrimitive(8);
        entity.setLongObject(1_000L);
        entity.setLongPrimitive(2_000L);
        entity.setShortObject((short) 100);
        entity.setShortPrimitive((short) 200);
        entity.setDoubleObject(25.0);
        entity.setDoublePrimitive(30.0);
        entity.setFloatObject(50.0f);
        entity.setFloatPrimitive(60.0f);
        entity.setBigInteger(BigInteger.valueOf(1_000_000));
        entity.setBigDecimal(BigDecimal.valueOf(2_000_000.007));
        entity.setInstant(LocalDateTime.of(2000, 12, 1, 14, 54).atZone(ZoneId.systemDefault()).toInstant());
        entity.setLocalDate(LocalDate.of(2010, 1, 1));
        entity.setLocalDateTime(LocalDateTime.of(2020, 1, 1, 1, 1));
        entity.setTstEnumOrdinal(MultipleFieldsEntity.TstEnum.VALUE_A);
        entity.setTstEnumString(MultipleFieldsEntity.TstEnum.VALUE_B);
        entity.getEmbeddedClass().setEmbeddedValue("embedded value");
        entity.setCoordinates(new Coordinates(new BigDecimal(49), new BigDecimal(21)));
        entity.setToOneRelationship(new SimpleKeyValueEntity("KEY", "VALUE"));
        entity.setToManyRelationship(List.of(
                new SimpleKeyValueEntity("KEY_1", "VALUE_1"),
                new SimpleKeyValueEntity("KEY_2", "VALUE_2")
        ));
        entity.setElementCollection(List.of("Tungsten", "Iron"));

        multipleFieldsRepository.create(entity);

        var found = multipleFieldsRepository.find(entity.getId());

        assertThat(found).isEqualTo(entity);
        assertThat(found).isEqualToComparingFieldByField(entity);
    }


}
