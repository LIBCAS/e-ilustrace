package cz.inqool.entityviews.subclass;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableLeaf;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.util.Set;

@ViewableClass(views = {"detail"})
@ViewableLeaf(subClasses = {Man.class, Woman.class})
@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes(value = {
        @Type(name = "MAN", value = Man.class),
        @Type(name = "WOMAN", value = Woman.class)
})
@Schema(
        oneOf = {Man.class, Woman.class},
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "MAN", schema = Man.class),
                @DiscriminatorMapping(value = "WOMAN", schema = Woman.class)
        }
)
public abstract class Person {

    public String firstName;

    @ViewableProperty(views = "detail")
    public String address;


    public abstract String getType();

    @ViewableProperty(views = "detail")
    @ViewableMapping(views = "detail", mappedTo = "detail")
    public abstract Set<? extends Person> getSelfs();
}
