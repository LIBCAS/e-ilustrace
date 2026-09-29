package cz.inqool.eas.common.dao.simple.multiple;

import cz.inqool.eas.common.dao.simple.keyvalue.SimpleKeyValueEntity;
import cz.inqool.eas.common.domain.store.DomainObject;
import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Test entity with multiple fields of different types
 *
 * @author : olda
 * @since : 02/10/2020, Fri
 **/
@Getter
@Setter
@Builder
@Entity
@Table(name = "multiple_fields_entity")
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class MultipleFieldsEntity extends DomainObject<MultipleFieldsEntity> {

    @Default
    private UUID uuidId = UUID.randomUUID();

    private String shortString;
    private String longString;
    private String blobText;

    private Boolean booleanObject;
    @Default
    private boolean booleanPrimitive = false;

    @Default
    private Character charObject = '?';
    @Default
    private char charPrimitive = '_';
    @Default
    private char[] charArray = null;

    private Byte byteObject;
    @Default
    private byte bytePrimitive = 0;
    @Default
    private byte[] byteArray = null;

    private Integer integerObject;
    @Default
    private int integerPrimitive = 0;

    private Long longObject;
    @Default
    private long longPrimitive = 0;

    private Short shortObject;
    @Default
    private short shortPrimitive = 0;

    private Double doubleObject;
    @Default
    private double doublePrimitive = 0.0;

    private Float floatObject;
    @Default
    private float floatPrimitive = 0.0f;

    private BigInteger bigInteger;
    private BigDecimal bigDecimal;

    private Instant instant;
    private LocalDate localDate;
    private LocalDateTime localDateTime;

    @Enumerated(EnumType.STRING)
    private TstEnum tstEnumString;
    @Enumerated(EnumType.ORDINAL)
    private TstEnum tstEnumOrdinal;

    @Default
    @Embedded
    private EmbeddedClass embeddedClass = new EmbeddedClass();

    @Embedded
    private Coordinates coordinates;

    @Fetch(FetchMode.SELECT)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "to_one_relationship_id")
    private SimpleKeyValueEntity toOneRelationship;

    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "multiple_fields_entity_simple_key_value_entities",
            joinColumns = @JoinColumn(name = "multiple_fields_entity_id"),
            inverseJoinColumns = @JoinColumn(name = "simple_key_value_id"))
    @BatchSize(size = 100)
    private List<SimpleKeyValueEntity> toManyRelationship;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "multiple_fields_entity_element_collection",
            joinColumns = @JoinColumn(name = "multiple_fields_entity_id"))
    @Column(name = "element")
    private List<String> elementCollection;


    public enum TstEnum {
        VALUE_A,
        VALUE_B,
        VALUE_C
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class EmbeddedClass {
        private String embeddedValue;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class Coordinates {
        private BigDecimal lat;
        private BigDecimal lon;
    }
}
