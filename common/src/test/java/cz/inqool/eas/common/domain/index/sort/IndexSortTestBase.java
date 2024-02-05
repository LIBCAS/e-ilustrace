package cz.inqool.eas.common.domain.index.sort;

import cz.inqool.eas.common.dao.simple.keyvalue.SimpleKeyValueEntity;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsEntity;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsEntity.Coordinates;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsEntity.EmbeddedClass;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsEntity.TstEnum;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsRepository;
import cz.inqool.eas.common.domain.index.DomainIndexedObject;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.tstutil.CommonTestBase;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import static cz.inqool.eas.common.exception.ExceptionUtils.checked;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
abstract public class IndexSortTestBase extends CommonTestBase {

    @Autowired
    protected MultipleFieldsRepository repository;

    protected MultipleFieldsEntity entity_1;
    protected MultipleFieldsEntity entity_2;
    protected MultipleFieldsEntity entity_3;

    private Resource extraLongTextFile;


    @Override
    public Set<Class<? extends DomainIndexedObject<?, ?>>> getIndexedObjectClasses() {
        return Set.of(MultipleFieldsIndexedObject.class);
    }

    @BeforeEach
    public void init() {
        String extraLongText = checked(() -> {
            Path filePath = Paths.get(extraLongTextFile.getURI());
            return Files.readString(filePath);
        });

        entity_1 = MultipleFieldsEntity.builder()
                .uuidId(UUID.fromString("3daf1258-79b8-40b4-8f01-363e74360956"))
                .shortString("Obecní úřad Bukovina")
                .longString("Probably more than some two words because we need to test some full-text searching " +
                        "functionality of ElasticSearch and what could be more suitable than an useless text like this. " +
                        "I'm the best random sentence generator. Lorem ipsum is nothing in comparison with me. I should also " +
                        "use some curse words like f*** or s*** to include some special characters in this sentence.")
                .blobText(extraLongText)
                .booleanObject(Boolean.TRUE)
                .booleanPrimitive(true)
                .integerObject(598)
                .integerPrimitive(784)
                .longObject(9_696L)
                .longPrimitive(1543L)
                .doubleObject(2.718281)
                .doublePrimitive(1_457_652_478.6258)
                .floatObject(9.81F)
                .floatPrimitive(299_792.458F)
                .bigInteger(BigInteger.valueOf(6_325))
                .bigDecimal(new BigDecimal("2014.75"))
                .instant(Instant.parse("2007-12-03T10:15:30.00Z"))
                .localDate(LocalDate.parse("2007-12-03"))
                .localDateTime(LocalDateTime.parse("2020-12-03T10:15:30"))
                .tstEnumString(TstEnum.VALUE_B)
                .tstEnumOrdinal(TstEnum.VALUE_B)
                .embeddedClass(new EmbeddedClass("some short text"))
                .coordinates(new Coordinates(BigDecimal.valueOf(21.2130332), BigDecimal.valueOf(49.2050547)))
                .toOneRelationship(SimpleKeyValueEntity.builder()
                        .value("Grandfather Steve")
                        .build())
                .toManyRelationship(List.of(
                        SimpleKeyValueEntity.builder()
                                .value("Son Luke")
                                .build(),
                        SimpleKeyValueEntity.builder()
                                .value("Son Mark")
                                .build()
                ))
                .elementCollection(List.of("A", "B", "C"))
                .build();
        entity_1.setId("FIRST_ENTITY_ID");

        entity_2 = MultipleFieldsEntity.builder()
                .uuidId(UUID.fromString("fe5c07cd-e03c-4a41-84ff-4c20f6fcdb38"))
                .shortString("Případ komisáře Rexa.")
                .longString("You are unbelievable. I don't want to write this meaningless piece of s*** so stop making me " +
                        "do things I hate.")
                .booleanObject(Boolean.FALSE)
                .booleanPrimitive(false)
                .integerObject(7_415_632)
                .integerPrimitive(1)
                .longObject(888_777_666_555L)
                .longPrimitive(59_475L)
                .doubleObject(3.1415)
                .doublePrimitive(1.1111)
                .floatObject(23.2323F)
                .floatPrimitive(0.487_866F)
                .bigInteger(BigInteger.valueOf(15))
                .bigDecimal(BigDecimal.ZERO)
                .instant(Instant.parse("2020-10-24T13:25:30.00Z"))
                .localDate(LocalDate.parse("2002-10-24"))
                .localDateTime(LocalDateTime.parse("2018-10-24T13:25:30"))
                .tstEnumString(TstEnum.VALUE_A)
                .tstEnumOrdinal(TstEnum.VALUE_A)
                .embeddedClass(new EmbeddedClass("a glass of wine"))
                .coordinates(new Coordinates(BigDecimal.valueOf(16.6094504), BigDecimal.valueOf(49.1970870)))
                .toOneRelationship(SimpleKeyValueEntity.builder()
                        .value("Grandmother Mary")
                        .build())
                .toManyRelationship(List.of(
                        SimpleKeyValueEntity.builder()
                                .value("Daughter Olivia")
                                .build(),
                        SimpleKeyValueEntity.builder()
                                .value("Daughter Charlotte")
                                .build()
                ))
                .elementCollection(List.of("X", "Y", "Z"))
                .build();
        entity_2.setId("SECOND_ENTITY_ID");

        entity_3 = MultipleFieldsEntity.builder()
                .uuidId(UUID.fromString("378072db-1774-42f0-b37c-2eba69f9efb7"))
                .shortString("No no no no")
                .longString("I'm too lazy to write this again, so right now I'm done basically.")
                .booleanObject(null)
                .booleanPrimitive(false)
                .integerObject(5_469)
                .integerPrimitive(54)
                .longObject(20L)
                .longPrimitive(20L)
                .doubleObject(987_654_321.123_456)
                .doublePrimitive(987_654_321.123_456)
                .floatObject(23.2323F)
                .floatPrimitive(0.487_866F)
                .bigInteger(BigInteger.valueOf(15))
                .bigDecimal(BigDecimal.valueOf(10_568L))
                .instant(Instant.parse("2019-07-19T00:00:00.00Z"))
                .localDate(LocalDate.parse("2019-04-19"))
                .localDateTime(LocalDateTime.parse("2002-02-20T20:02:20"))
                .tstEnumString(TstEnum.VALUE_C)
                .tstEnumOrdinal(TstEnum.VALUE_C)
                .embeddedClass(new EmbeddedClass("This is not that funny I've expected."))
                .coordinates(new Coordinates(BigDecimal.valueOf(20.1390913), BigDecimal.valueOf(48.8829043)))
                .toOneRelationship(SimpleKeyValueEntity.builder()
                        .value("God")
                        .build())
                .toManyRelationship(List.of(
                        SimpleKeyValueEntity.builder()
                                .value("Cat Tom")
                                .build()
                ))
                .elementCollection(List.of("M"))
                .build();
        entity_3.setId("THIRD_ENTITY_ID");

        repository.create(List.of(entity_1, entity_2, entity_3));
    }

    protected void assertMatchesOrder(Supplier<Result<MultipleFieldsEntity>> searchMethod, MultipleFieldsEntity first, MultipleFieldsEntity second, MultipleFieldsEntity third) {
        Result<MultipleFieldsEntity> result = searchMethod.get();
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(3, result.getItems().size());

        assertThat(result.getItems(), Matchers.contains(
                hasProperty("id", equalTo(first.getId())),
                hasProperty("id", equalTo(second.getId())),
                hasProperty("id", equalTo(third.getId()))
        ));
    }


    @Autowired
    public void setExtraLongTextFile(@Value("classpath:text/extraLongText.txt") Resource file) {
        this.extraLongTextFile = file;
    }
}
