package cz.inqool.eas.eil.user;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.inqool.eas.common.domain.DomainViews.*;
import static cz.inqool.eas.eil.user.User.ESSENTIAL;

@Viewable
@DomainViews
@ViewableClass(views = {DEFAULT, ESSENTIAL}, generateRef = true)
@ViewableMapping(views = {DEFAULT, ESSENTIAL}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {DEFAULT, ESSENTIAL})
@FieldNameConstants
@Getter
@Setter
@Entity
@Table(name = "eil_user")
public class User extends DatedObject<User> {
    public static final String ESSENTIAL = "ESSENTIAL";

    @ViewableProperty(views = {DETAIL, LIST, CREATE})
    @Email
    @NotNull
    String email;

    @ViewableProperty(views = {DETAIL, LIST, CREATE, UPDATE, ESSENTIAL})
    @NotNull
    String firstName;

    @ViewableProperty(views = {DETAIL, LIST, CREATE, UPDATE, ESSENTIAL})
    @NotNull
    String lastName;

    @ViewableProperty(views = {CREATE})
    @NotNull
    String password;

    @ViewableProperty(views = {DETAIL, LIST})
    @Enumerated(value = EnumType.STRING)
    @NotNull
    EilRole role;

    @ViewableProperty(views = {DETAIL, LIST})
    boolean validated;

    @ViewableProperty()
    String emailConfirmationKey;

    @ViewableProperty(views = {DETAIL, LIST, ESSENTIAL})
    public String getFullName() {
        return Stream.of(firstName, lastName).filter(Objects::nonNull).collect(Collectors.joining(" "));
    }
}
