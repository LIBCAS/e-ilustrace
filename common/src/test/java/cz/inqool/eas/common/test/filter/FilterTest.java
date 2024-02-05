package cz.inqool.eas.common.test.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsEntity;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsRepository;
import cz.inqool.eas.common.domain.index.DomainIndexedObject;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.filter.*;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.domain.index.dto.sort.FieldSort;
import cz.inqool.eas.common.tstutil.CommonTestBase;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for testing elasticsearch filters
 *
 * @author : olda
 * @since : 05/10/2020, Mon
 **/
@Transactional
public class FilterTest extends CommonTestBase {

    @Autowired
    private MultipleFieldsRepository repository;

    MultipleFieldsEntity one;
    MultipleFieldsEntity two;
    MultipleFieldsEntity three;

    @Override
    protected Set<Class<? extends DomainIndexedObject<?, ?>>> getIndexedObjectClasses() {
        return Set.of(MultipleFieldsIndexedObject.class);
    }

    @BeforeEach
    public void setUp() {
        one = new MultipleFieldsEntity();
        two = new MultipleFieldsEntity();
        three = new MultipleFieldsEntity();
    }

    @Test
    public void filter_by_UUID() {
        var uuid = UUID.fromString("59f1701c-56b2-4bb0-a8f9-88f1475260aa");
        one.setUuidId(uuid);
        two.setUuidId(uuid);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("uuidId", uuid.toString()));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new ContainsFilter("uuidId", "56b2-4bb0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new NotFilter(new EqFilter("uuidId", uuid.toString())));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(three);

        params = new Params();
        params.addFilter(new StartWithFilter("uuidId", "59f1701c"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);
    }

    @Test
    public void sort_by_UUID() {
        var uuid1 = UUID.fromString("29f1701c-56b2-4bb0-a8f9-88f1475260aa");
        one.setUuidId(uuid1);
        var uuid2 = UUID.fromString("3e369176-4586-4b5e-8d63-36d90dc7923c");
        two.setUuidId(uuid2);
        var uuid3 = UUID.fromString("3e369176-4586-4b5e-8d63-36d90dc7923d");
        three.setUuidId(uuid3);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.uuidId, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_shortString() {
        one.setShortString("one");
        two.setShortString("two");
        three.setShortString("three");
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("shortString", one.getShortString()));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new ContainsFilter("shortString", "e"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, three);

        params = new Params();
        params.addFilter(new StartWithFilter("shortString", "t"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two, three);

        params = new Params();
        params.addFilter(new ContainsFilter("shortString", "xxx"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    public void sort_by_shortString() {
        one.setShortString("A");
        two.setShortString("á");
        three.setShortString("B");
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.shortString, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_longString() {
        one.setLongString("one");
        two.setLongString("two");
        three.setLongString("three");
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("longString", one.getLongString()));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new ContainsFilter("longString", "e"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, three);

        params = new Params();
        params.addFilter(new StartWithFilter("longString", "t"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two, three);

        params = new Params();
        params.addFilter(new ContainsFilter("longString", "xxx"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    public void sort_by_longString() {
        one.setLongString("n");
        two.setLongString("ň");
        three.setLongString("o");
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.longString, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_booleanObject() {
        one.setBooleanObject(true);
        two.setBooleanObject(false);
        three.setBooleanObject(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("booleanObject", "true"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("booleanObject", "false"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new NotNullFilter("booleanObject"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new NullFilter("booleanObject"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(three);
    }

    @Test
    public void sort_by_booleanObject() {
        one.setBooleanObject(true);
        two.setBooleanObject(false);
        three.setBooleanObject(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.booleanObject, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(two, one, three);
    }

    @Test
    public void filter_by_booleanPrimitive() {
        one.setBooleanPrimitive(true);
        two.setBooleanPrimitive(false);
        repository.create(Set.of(one, two));

        var params = new Params();
        params.addFilter(new EqFilter("booleanPrimitive", "true"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("booleanPrimitive", "false"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);
    }

    @Test
    public void sort_by_booleanPrimitive() {
        one.setBooleanPrimitive(true);
        two.setBooleanPrimitive(false);
        repository.create(Set.of(one, two));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.booleanPrimitive, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(two, one);
    }

    @Test
    public void filter_by_byteObject() {
        one.setByteObject((byte) -10);
        two.setByteObject((byte) 0);
        three.setByteObject((byte) 10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("byteObject", "-10"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("byteObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("byteObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("byteObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(three);
    }

    @Test
    public void sort_by_byteObject() {
        one.setByteObject((byte) -10);
        two.setByteObject((byte) 0);
        three.setByteObject((byte) 10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.byteObject, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_bytePrimitive() {
        one.setBytePrimitive((byte) -10);
        two.setBytePrimitive((byte) 0);
        three.setBytePrimitive((byte) 10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("bytePrimitive", "-10"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("bytePrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("bytePrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("bytePrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(three);
    }

    @Test
    public void sort_by_bytePrimitive() {
        one.setBytePrimitive((byte) -10);
        two.setBytePrimitive((byte) 0);
        three.setBytePrimitive((byte) 10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.bytePrimitive, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_integerObject() {
        one.setIntegerObject(-10);
        two.setIntegerObject(0);
        three.setIntegerObject(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("integerObject", "-10"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("integerObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("integerObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("integerObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    public void sort_by_integerObject() {
        one.setIntegerObject(-10);
        two.setIntegerObject(0);
        three.setIntegerObject(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.integerObject, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_integerPrimitive() {
        one.setIntegerPrimitive(-10);
        two.setIntegerPrimitive(0);
        three.setIntegerPrimitive(10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("integerPrimitive", "-10"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("integerPrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("integerPrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("integerPrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(three);
    }

    @Test
    public void sort_by_integerPrimitive() {
        one.setIntegerPrimitive(-10);
        two.setIntegerPrimitive(0);
        three.setIntegerPrimitive(10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.integerPrimitive, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_longObject() {
        one.setLongObject(-10L);
        two.setLongObject(0L);
        three.setLongObject(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("longObject", "-10"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("longObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("longObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("longObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    public void sort_by_longObject() {
        one.setLongObject(-10L);
        two.setLongObject(0L);
        three.setLongObject(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.longObject, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_longPrimitive() {
        one.setLongPrimitive(-10);
        two.setLongPrimitive(0);
        three.setLongPrimitive(10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("longPrimitive", "-10"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("longPrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("longPrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("longPrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(three);
    }

    @Test
    public void sort_by_longPrimitive() {
        one.setLongPrimitive(-10);
        two.setLongPrimitive(0);
        three.setLongPrimitive(10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.longPrimitive, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_shortObject() {
        one.setShortObject((short) -10);
        two.setShortObject((short) 0);
        three.setShortObject(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("shortObject", "-10"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("shortObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("shortObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("shortObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    public void sort_by_shortObject() {
        one.setShortObject((short) -10);
        two.setShortObject((short) 0);
        three.setShortObject(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.shortObject, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_shortPrimitive() {
        one.setShortPrimitive((short) -10);
        two.setShortPrimitive((short) 0);
        three.setShortPrimitive((short) 10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("shortPrimitive", "-10"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("shortPrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("shortPrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("shortPrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(three);
    }

    @Test
    public void sort_by_shortPrimitive() {
        one.setShortPrimitive((short) -10);
        two.setShortPrimitive((short) 0);
        three.setShortPrimitive((short) 10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.shortPrimitive, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_doubleObject() {
        one.setDoubleObject(-10.0);
        two.setDoubleObject(0.0);
        three.setDoubleObject(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("doubleObject", "-10.0"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("doubleObject", "0.0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("doubleObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("doubleObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    public void sort_by_doubleObject() {
        one.setDoubleObject(-10.0);
        two.setDoubleObject(0.0);
        three.setDoubleObject(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.doubleObject, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_doublePrimitive() {
        one.setDoublePrimitive(-10);
        two.setDoublePrimitive(0);
        three.setDoublePrimitive(10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("doublePrimitive", "-10"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("doublePrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("doublePrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("doublePrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(three);
    }

    @Test
    public void sort_by_doublePrimitive() {
        one.setDoublePrimitive(-10);
        two.setDoublePrimitive(0);
        three.setDoublePrimitive(10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.doublePrimitive, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_floatObject() {
        one.setFloatObject(-10.0f);
        two.setFloatObject(0.0f);
        three.setFloatObject(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("floatObject", "-10.0"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("floatObject", "0.0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("floatObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("floatObject", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    public void sort_by_floatObject() {
        one.setFloatObject(-10.0f);
        two.setFloatObject(0.0f);
        three.setFloatObject(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.floatObject, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_floatPrimitive() {
        one.setFloatPrimitive(-10);
        two.setFloatPrimitive(0);
        three.setFloatPrimitive(10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("floatPrimitive", "-10"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("floatPrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("floatPrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("floatPrimitive", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(three);
    }

    @Test
    public void sort_by_floatPrimitive() {
        one.setFloatPrimitive(-10);
        two.setFloatPrimitive(0);
        three.setFloatPrimitive(10);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.floatPrimitive, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_bigInteger() {
        one.setBigInteger(BigInteger.valueOf(-10));
        two.setBigInteger(BigInteger.valueOf(0));
        three.setBigInteger(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("bigInteger", "-10"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("bigInteger", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("bigInteger", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("bigInteger", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    public void sort_by_bigInteger() {
        one.setBigInteger(BigInteger.valueOf(-10));
        two.setBigInteger(BigInteger.valueOf(0));
        three.setBigInteger(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.bigInteger, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_bigDecimal() {
        one.setBigDecimal(BigDecimal.valueOf(-10));
        two.setBigDecimal(BigDecimal.valueOf(0));
        three.setBigDecimal(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("bigDecimal", "-10"));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("bigDecimal", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("bigDecimal", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two);

        params = new Params();
        params.addFilter(new GtFilter("bigDecimal", "0"));
        result = repository.listByParams(params);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    public void sort_by_bigDecimal() {
        one.setBigDecimal(BigDecimal.valueOf(-10));
        two.setBigDecimal(BigDecimal.valueOf(0));
        three.setBigDecimal(null);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.bigDecimal, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }


    @Test
    public void filter_by_instant() {
        var insta1 = Instant.ofEpochSecond(0);
        one.setInstant(insta1);
        var insta2 = Instant.ofEpochSecond(100);
        two.setInstant(insta2);
        var insta3 = Instant.ofEpochSecond(200);
        three.setInstant(insta3);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("instant", LocalDateTime.ofInstant(insta1, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("instant", LocalDateTime.ofInstant(insta2, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("instant", LocalDateTime.ofInstant(insta1, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new GtFilter("instant", LocalDateTime.ofInstant(insta1, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two, three);

        params = new Params();
        params.addFilter(new AndFilter(
                new GteFilter("instant", LocalDateTime.ofInstant(insta1, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))),
                new LteFilter("instant", LocalDateTime.ofInstant(insta3, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")))
        ));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two, three);
    }

    @Test
    public void sort_by_instant() {
        var insta1 = Instant.ofEpochSecond(0);
        one.setInstant(insta1);
        var insta2 = Instant.ofEpochSecond(100);
        two.setInstant(insta2);
        var insta3 = Instant.ofEpochSecond(200);
        three.setInstant(insta3);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.instant, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_localDate() {
        var ld1 = LocalDate.of(2000, 1, 1);
        one.setLocalDate(ld1);
        var ld2 = LocalDate.of(2000, 1, 2);
        two.setLocalDate(ld2);
        var ld3 = LocalDate.of(2001, 1, 1);
        three.setLocalDate(ld3);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("localDate", ld1.toString()));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("localDate", ld2.toString()));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("localDate", ld1.toString()));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new GtFilter("localDate", ld1.toString()));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two, three);

        params = new Params();
        params.addFilter(new AndFilter(
                new GteFilter("localDate", ld1.toString()),
                new LteFilter("localDate", ld3.toString())
        ));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two, three);
    }

    @Test
    public void sort_by_localDate() {
        var ld1 = LocalDate.of(2000, 1, 1);
        one.setLocalDate(ld1);
        var ld2 = LocalDate.of(2000, 1, 2);
        two.setLocalDate(ld2);
        var ld3 = LocalDate.of(2001, 1, 1);
        three.setLocalDate(ld3);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.localDate, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }

    @Test
    public void filter_by_localDateTime() {
        var ld1 = LocalDateTime.of(2000, 1, 1, 1, 1, 1);
        one.setLocalDateTime(ld1);
        var ld2 = LocalDateTime.of(2000, 1, 2, 0, 0, 0);
        two.setLocalDateTime(ld2);
        var ld3 = LocalDateTime.of(2001, 1, 1, 0, 0, 0);
        three.setLocalDateTime(ld3);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addFilter(new EqFilter("localDateTime", ld1.format(DateTimeFormatter.ISO_DATE_TIME)));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new EqFilter("localDateTime", ld2.format(DateTimeFormatter.ISO_DATE_TIME)));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two);

        params = new Params();
        params.addFilter(new LteFilter("localDateTime", ld1.format(DateTimeFormatter.ISO_DATE_TIME)));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one);

        params = new Params();
        params.addFilter(new GtFilter("localDateTime", ld1.format(DateTimeFormatter.ISO_DATE_TIME)));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(two, three);

        params = new Params();
        params.addFilter(new AndFilter(
                new GteFilter("localDateTime", ld1.format(DateTimeFormatter.ISO_DATE_TIME)),
                new LteFilter("localDateTime", ld3.format(DateTimeFormatter.ISO_DATE_TIME))
        ));
        result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactlyInAnyOrder(one, two, three);
    }

    @Test
    public void sort_by_localDateTime() {
        var ld1 = LocalDateTime.of(2000, 1, 1, 1, 1, 1);
        one.setLocalDateTime(ld1);
        var ld2 = LocalDateTime.of(2000, 1, 2, 0, 0, 0);
        two.setLocalDateTime(ld2);
        var ld3 = LocalDateTime.of(2001, 1, 1, 0, 0, 0);
        three.setLocalDateTime(ld3);
        repository.create(Set.of(one, two, three));

        var params = new Params();
        params.addSort(new FieldSort(IndexFields.localDateTime, SortOrder.ASC));
        Result<MultipleFieldsEntity> result = repository.listByParams(params);
        assertThat(result.getItems()).containsExactly(one, two, three);
    }
}
