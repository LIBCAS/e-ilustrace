package cz.inqool.eas.common.dictionary.store;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.dictionary.Dictionary;
import cz.inqool.eas.common.dictionary.reference.Coded;
import cz.inqool.eas.common.domain.index.reference.Labeled;
import cz.inqool.eas.common.multiString.MultiString;
import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableImplement;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Nationalized;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

import static cz.inqool.eas.common.dictionary.DictionaryViews.LABELED;
import static cz.inqool.eas.common.domain.DomainViews.*;

@ViewableClass(views = {DEFAULT, CREATE, UPDATE, IDENTIFIED, LABELED})
@ViewableMapping(views = DEFAULT, mappedTo = DEFAULT)
@ViewableMapping(views = CREATE, mappedTo = CREATE)
@ViewableMapping(views = UPDATE, mappedTo = UPDATE)
@ViewableMapping(views = {LABELED, IDENTIFIED}, mappedTo = IDENTIFIED)
@ViewableImplement(value = {Dictionary.class, Labeled.class, Coded.class}, views = {DEFAULT})
@ViewableImplement(value = {Labeled.class}, views = {LABELED})
@Getter
@Setter
@MappedSuperclass
@FieldNameConstants
abstract public class DictionaryObject<ROOT> extends AuthoredObject<ROOT> implements Dictionary<ROOT>, Labeled, Coded {
    /**
     * Name
     */
    @ViewableProperty(views = {DEFAULT, CREATE, UPDATE, LABELED})
    protected String name;

    /**
     * Name
     */
    @ViewableProperty(views = {DEFAULT, CREATE, UPDATE, LABELED})
    @Nationalized
    protected MultiString multiName;

    /**
     * Active flag
     */
    @ViewableProperty(views = {CREATE, DEFAULT})
    protected boolean active;

    /**
     * Valid from
     */
    @ViewableProperty(views = {DEFAULT, CREATE, UPDATE})
    @Nullable
    protected Instant validFrom;

    /**
     * Valid to
     */
    @ViewableProperty(views = {DEFAULT, CREATE, UPDATE})
    @Nullable
    protected Instant validTo;

    @ViewableProperty(views = {DEFAULT, CREATE, UPDATE})
    @Nullable
    @Column(name = "list_order")
    protected Integer order;

    protected String code;

    /**
     * Checks current date and time against active flag and valid dates.
     */
    @ViewableProperty(views = DEFAULT)
    public boolean isValidAndActive() {
        if (deleted != null) {
            return false;
        }

        if (!active) {
            return false;
        }

        Instant now = Instant.now();

        if (validFrom != null && validFrom.isAfter(now)) {
            return false;
        }

        if (validTo != null && validTo.isBefore(now)) {
            return false;
        }

        return true;
    }

    @ViewableProperty(views = {DEFAULT, LABELED})
    @Override
    public String getLabel() {
        return name;
    }
}
