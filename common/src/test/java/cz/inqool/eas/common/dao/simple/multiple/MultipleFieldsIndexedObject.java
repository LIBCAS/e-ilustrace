package cz.inqool.eas.common.dao.simple.multiple;

import cz.inqool.eas.common.dao.simple.keyvalue.SimpleKeyValueIndexedObject;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsEntity.Coordinates;
import cz.inqool.eas.common.domain.index.DomainIndexedObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

/**
 * Indexed class for {@link MultipleFieldsEntity}
 *
 * @author : olda
 * @since : 02/10/2020, Fri
 **/
@Getter
@Setter
@Document(indexName = "eas_multiple_fields_entity")
@FieldNameConstants(innerTypeName = "IndexFields")
public class MultipleFieldsIndexedObject extends DomainIndexedObject<MultipleFieldsEntity, MultipleFieldsEntity> {

    @Field(type = FieldType.Keyword)
    private UUID uuidId;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String shortString;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String longString;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String blobText;

    @Field(type = FieldType.Boolean)
    private Boolean booleanObject;
    @Field(type = FieldType.Boolean)
    private boolean booleanPrimitive;

//    @Field(type = FieldType.Keyword)
//    private Character charObject;
//    @Field(type = FieldType.Keyword)
//    private char charPrimitive;
//    @Field(type = FieldType.Keyword)
//    private char[] charArray;

    @Field(type = FieldType.Byte)
    private Byte byteObject;
    @Field(type = FieldType.Byte)
    private byte bytePrimitive;

    @Field(type = FieldType.Integer)
    private Integer integerObject;
    @Field(type = FieldType.Integer)
    private int integerPrimitive;

    @Field(type = FieldType.Long)
    private Long longObject;
    @Field(type = FieldType.Long)
    private long longPrimitive;

    @Field(type = FieldType.Short)
    private Short shortObject;
    @Field(type = FieldType.Short)
    private short shortPrimitive;

    @Field(type = FieldType.Double)
    private Double doubleObject;
    @Field(type = FieldType.Double)
    private double doublePrimitive;

    @Field(type = FieldType.Float)
    private Float floatObject;
    @Field(type = FieldType.Float)
    private float floatPrimitive;

    @Field(type = FieldType.Long)
    private Long bigInteger;
    @Field(type = FieldType.Double)
    private BigDecimal bigDecimal;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant instant;
    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate localDate;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime localDateTime;

    @Field(type = FieldType.Keyword)
    private MultipleFieldsEntity.TstEnum tstEnumString;
    @Field(type = FieldType.Keyword)
    private MultipleFieldsEntity.TstEnum tstEnumOrdinal;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String embeddedClass_embeddedValue;

    @GeoPointField
    private GeoPoint coordinates;

    @Field(type = FieldType.Object)
    private SimpleKeyValueIndexedObject toOneRelationship;

    @Field(type = FieldType.Nested)
    private SimpleKeyValueIndexedObject toOneRelationshipNested;

    @Field(type = FieldType.Nested)
    private List<SimpleKeyValueIndexedObject> toManyRelationship;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private List<String> elementCollection;


    @Override
    public void toIndexedObject(MultipleFieldsEntity obj) {
        super.toIndexedObject(obj);

        this.uuidId = obj.getUuidId();

        this.shortString = obj.getShortString();
        this.longString = obj.getLongString();
        this.blobText = obj.getBlobText();

        this.booleanObject = obj.getBooleanObject();
        this.booleanPrimitive = obj.isBooleanPrimitive();

        this.byteObject = obj.getByteObject();
        this.bytePrimitive = obj.getBytePrimitive();

        this.integerObject = obj.getIntegerObject();
        this.integerPrimitive = obj.getIntegerPrimitive();

        this.longObject = obj.getLongObject();
        this.longPrimitive = obj.getLongPrimitive();

        this.shortObject = obj.getShortObject();
        this.shortPrimitive = obj.getShortPrimitive();

        this.doubleObject = obj.getDoubleObject();
        this.doublePrimitive = obj.getDoublePrimitive();

        this.floatObject = obj.getFloatObject();
        this.floatPrimitive = obj.getFloatPrimitive();

        if (obj.getBigInteger() != null) {
            this.bigInteger = obj.getBigInteger().longValue();
        }
        this.bigDecimal = obj.getBigDecimal();

        this.instant = obj.getInstant();
        this.localDate = obj.getLocalDate();
        this.localDateTime = obj.getLocalDateTime();

        this.tstEnumOrdinal = obj.getTstEnumOrdinal();
        this.tstEnumString = obj.getTstEnumString();

        this.embeddedClass_embeddedValue = obj.getEmbeddedClass().getEmbeddedValue();

        if (obj.getCoordinates() != null) {
            Coordinates coordinates = obj.getCoordinates();
            this.coordinates = new GeoPoint(coordinates.getLat().doubleValue(), coordinates.getLon().doubleValue());
        }

        if (obj.getToOneRelationship() != null) {
            SimpleKeyValueIndexedObject indexedObject = new SimpleKeyValueIndexedObject();
            indexedObject.toIndexedObject(obj.getToOneRelationship());
            this.toOneRelationship = indexedObject;
            this.toOneRelationshipNested = indexedObject;
        }

        if (obj.getToManyRelationship() != null) {
            this.toManyRelationship = obj.getToManyRelationship().stream()
                    .map(entity -> {
                        SimpleKeyValueIndexedObject indexed = new SimpleKeyValueIndexedObject();
                        indexed.toIndexedObject(entity);
                        return indexed;
                    }).collect(Collectors.toList());
        }

        this.elementCollection = obj.getElementCollection();
    }
}
